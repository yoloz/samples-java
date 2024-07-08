package indi.yolo.sample.oracle.jdbc.debug.entity;

/**
 * line#: Duplicate of program.line#
 * terminated: Whether the program has terminated
 * breakpoint: Breakpoint number
 * stackdepth: Number of frames on the stack
 * interpreterdepth: [A reserved field]
 * reason: Reason for suspension
 * program: Source location(program_info)
 *
 * @author yolo
 */
public class RunTimeInfo {

    private final Integer line;
    private final Integer terminated;
    private final Integer breakpoint;
    private final Integer stackdepth;
    private final Integer interpreterdepth;
    private final Integer reason;
    private final ProgramInfo program;

    public RunTimeInfo(Integer line, Integer terminated, Integer breakpoint, Integer stackdepth, Integer interpreterdepth,
                       Integer reason, ProgramInfo program) {
        this.line = line;
        this.terminated = terminated;
        this.breakpoint = breakpoint;
        this.stackdepth = stackdepth;
        this.interpreterdepth = interpreterdepth;
        this.reason = reason;
        this.program = program;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getTerminated() {
        return terminated;
    }

    public Integer getBreakpoint() {
        return breakpoint;
    }

    public Integer getStackdepth() {
        return stackdepth;
    }

    public Integer getInterpreterdepth() {
        return interpreterdepth;
    }

    public Integer getReason() {
        return reason;
    }

    public ProgramInfo getProgram() {
        return program;
    }

    @Override
    public String toString() {
        return "RunTimeInfo{" +
                "line=" + line +
                ", terminated=" + terminated +
                ", breakpoint=" + breakpoint +
                ", stackdepth=" + stackdepth +
                ", interpreterdepth=" + interpreterdepth +
                ", reason=" + SuspensionFlags.getReason(reason) +
                ", program=" + program +
                '}';
    }
}
