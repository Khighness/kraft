package top.parak.kraft.core.log;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class LogGenerationTest {

    @Test
    public void testIsValidName() {
        Assert.assertTrue(LogGeneration.isValidDirName("log-0"));
        Assert.assertTrue(LogGeneration.isValidDirName("log-1"));
        Assert.assertTrue(LogGeneration.isValidDirName("log-12"));
        Assert.assertFalse(LogGeneration.isValidDirName("log-"));
        Assert.assertFalse(LogGeneration.isValidDirName("foo"));
        Assert.assertFalse(LogGeneration.isValidDirName("foo-1"));
    }

    @Test
    public void testCreateFromFile() {
        LogGeneration logGeneration = new LogGeneration(new File("log-1"));
        Assert.assertEquals(1, logGeneration.getLastIncludedIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFromFileFailed() {
        new LogGeneration(new File("foo-1"));
    }

    @Test
    public void testCreateWithBaseDir() {
        LogGeneration logGeneration = new LogGeneration(new File("data"), 10);
        Assert.assertEquals(10, logGeneration.getLastIncludedIndex());
        Assert.assertEquals("log-10", logGeneration.get().getName());
    }

    @Test
    public void testCompare() {
        File baseDir = new File("data");
        LogGeneration logGeneration = new LogGeneration(baseDir, 10);
        Assert.assertEquals(1, logGeneration.compareTo(new LogGeneration(baseDir, 9)));
        Assert.assertEquals(0, logGeneration.compareTo(new LogGeneration(baseDir, 10)));
        Assert.assertEquals(-1, logGeneration.compareTo(new LogGeneration(baseDir, 11)));
    }

    @Test
    public void testGetFile() {
        LogGeneration logGeneration = new LogGeneration(new File("data"), 20);
        Assert.assertEquals(RootDir.FILE_NAME_SNAPSHOT, logGeneration.getSnapshotFile().getName());
        Assert.assertEquals(RootDir.FILE_NAME_ENTRIES, logGeneration.getEntriesFile().getName());
        Assert.assertEquals(RootDir.FILE_NAME_ENTRY_OFFSET_INDEX, logGeneration.getEntryOffsetIndexFile().getName());
    }

}