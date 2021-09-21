/*
 * Copyright (c) 2006-2007 Hyperic, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yoloz.sample.sigar.gather.system;

import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.SigarCommandBase;

import java.io.File;

/**
 * Display network info.
 */
public class NetInfo extends SigarCommandBase {

    private static final Sigar sigar;

    static {
        System.setProperty("java.library.path", new File("").getAbsolutePath() +
                "/java/functions/gatherData/lib/sigar");
        sigar = new Sigar();
    }


    public NetInfo(Shell shell) {
        super(shell);
    }

    public NetInfo() {
        super();
    }

    public String getUsageShort() {
        return "Display network info";
    }

    public void output(String[] args) throws SigarException {
        String[] interfaces = sigar.getNetInterfaceList();

        for(int i = 0; i < interfaces.length; ++i) {
            String name = interfaces[i];

            NetInterfaceConfig ifconfig;
            try {
                ifconfig = sigar.getNetInterfaceConfig(name);
                long flags = ifconfig.getFlags();
                if ((flags & 1L) > 0L && (flags & 16L) <= 0L && (flags & 8L) <= 0L) {
                    println("primary interface....." +
                            ifconfig.getName());

                    println("primary ip address...." +
                            ifconfig.getAddress());

                    println("primary mac address..." +
                            ifconfig.getHwaddr());

                    println("primary netmask......." +
                            ifconfig.getNetmask());

                    org.hyperic.sigar.NetInfo info =
                            sigar.getNetInfo();

                    println("host name............." +
                            info.getHostName());

                    println("domain name..........." +
                            info.getDomainName());

                    println("default gateway......." +
                            info.getDefaultGateway());

                    println("primary dns..........." +
                            info.getPrimaryDns());

                    println("secondary dns........." +
                            info.getSecondaryDns());
                }
            } catch (SigarException var7) {
                continue;
            }
        }

    }

    public static void main(String[] args) throws Exception {
        new NetInfo().processCommand(args);
    }
}
