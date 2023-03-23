package indi.yolo.sample.instrument;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

/**
 * App 运行后运行
 *
 * @author yoloz
 */
public class SuffMain {

    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.dir"));
        List<VirtualMachineDescriptor> virList = VirtualMachine.list();
        for (VirtualMachineDescriptor descriptor : virList) {
            if (descriptor.displayName().endsWith("App")) {
                VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
                virtualMachine.loadAgent(System.getProperty("user.dir") +
                        "//sample-instrument/target/sample-instrument-1.0.jar");
                virtualMachine.detach();
            }
        }
    }
}
