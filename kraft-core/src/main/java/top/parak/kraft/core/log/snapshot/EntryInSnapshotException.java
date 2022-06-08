package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.log.LogException;

public class EntryInSnapshotException extends LogException {

    private final int index;

    public EntryInSnapshotException(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
