package indi.yolo.sample.jsch;

import indi.yolo.sample.jsch.impl.AbstractFtp;
import indi.yolo.sample.jsch.impl.Ftp;
import indi.yolo.sample.jsch.impl.Sftp;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Arrays;


public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("pro").longOpt("protocol").required().hasArg().desc("协议[sftp,ftp]")
                .build());
        options.addOption(Option.builder("usr").longOpt("username").required().hasArg().desc("用户名").build());
        options.addOption(Option.builder("pwd").longOpt("password").required().hasArg().desc("密码").build());
        options.addOption(Option.builder("hst").longOpt("host").required().hasArg().desc("地址").build());
        options.addOption(Option.builder("prt").longOpt("port").required().hasArg().desc("端口").type(Number.class)
                .build());
        options.addOption(Option.builder("lp").longOpt("localpath").required().hasArg().desc("本地路径").build());
        options.addOption(Option.builder("rp").longOpt("remotepath").required().hasArg().desc("远程路径").build());
        options.addOption(Option.builder("up").longOpt("upload").desc("执行上传操作,默认下载操作").build());
        options.addOption(Option.builder("oder").longOpt("order").hasArg()
                .desc("文件名[fname]或修改时间[mtime]增量下载").build());
        options.addOption(Option.builder("sid").longOpt("jobid").hasArg().desc("增量下载任务ID").build());
        options.addOption(Option.builder("pa").longOpt("passive").desc("ftp被动模式,默认主动模式").build());
        options.addOption(Option.builder("epsv").longOpt("useEpsvWithIPv4").desc("ftp连接是否使用epsv,默认不使用")
                .build());
        options.addOption(Option.builder("idt").longOpt("idletime").hasArg().desc(
                "ftp数据传输时发送控制连接keepalive消息之间的等待时间,默认300(单位秒)").build());
        options.addOption(Option.builder("sot").longOpt("soTimeout").hasArg()
                .desc("设置socket连接的超时时间,默认15(单位秒)").build());
        if (args == null || args.length == 0 || "-h".equals(args[0]) || "-help".equals(args[0])) {
            HelpFormatter helper = new HelpFormatter();
            helper.printHelp("jsch.sh options...", options);
            System.exit(1);
        }
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            AbstractFtp abstractFtp;
            if ("sftp".equals(cmd.getOptionValue("pro"))) {
                abstractFtp = new Sftp(cmd.getOptionValue("usr"), cmd.getOptionValue("pwd"), cmd.getOptionValue("hst"),
                        ((Number) cmd.getParsedOptionValue("prt")).intValue(), cmd.getOptionValue("lp"),
                        cmd.getOptionValue("rp"), cmd.getOptionValue("oder", ""),
                        cmd.getOptionValue("sid", ""),
                        Integer.parseInt(cmd.getOptionValue("sot", "15")) * 1000);
            } else if ("ftp".equals(cmd.getOptionValue("pro"))) {
                abstractFtp = new Ftp(cmd.getOptionValue("usr"), cmd.getOptionValue("pwd"), cmd.getOptionValue("hst"),
                        ((Number) cmd.getParsedOptionValue("prt")).intValue(), cmd.getOptionValue("lp"),
                        cmd.getOptionValue("rp"), cmd.getOptionValue("oder", ""),
                        cmd.getOptionValue("sid", ""),
                        Integer.parseInt(cmd.getOptionValue("sot", "15")) * 1000,
                        cmd.hasOption("pa"), cmd.hasOption("epsv"),
                        Integer.parseInt(cmd.getOptionValue("idt", "300")));
            } else throw new Exception("协议[" + cmd.getOptionValue("pro") + "]未实现...");
            abstractFtp.login();
            try {
                if (cmd.hasOption("up")) abstractFtp.upload();
                else abstractFtp.download();
            } finally {
                abstractFtp.logout();
            }
        } catch (Exception e) {
            if (e instanceof MissingOptionException) {
                System.out.println("Missing required option:" +
                        Arrays.toString(((MissingOptionException) e).getMissingOptions().toArray()));
                HelpFormatter helper = new HelpFormatter();
                helper.printHelp("jsch.sh options...", options);
            } else {
                System.out.println("jsch error: " + e.getMessage());
            }
            System.exit(1);
        }

    }
}
