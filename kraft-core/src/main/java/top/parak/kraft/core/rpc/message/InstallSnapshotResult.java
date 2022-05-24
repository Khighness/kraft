package top.parak.kraft.core.rpc.message;

/**
 * InstallSnapshot RPC result.
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public class InstallSnapshotResult {

    /**
     * Current term, for leader to update itself.
     */
    private final int term;

    public InstallSnapshotResult(int term) {
        this.term = term;
    }

    public int getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "InstallSnapshotResult{" +
                "term=" + term +
                '}';
    }

}
