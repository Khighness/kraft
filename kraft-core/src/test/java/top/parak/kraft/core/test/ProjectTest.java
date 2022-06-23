package top.parak.kraft.core.test;

import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author KHighness
 * @since 2022-06-09
 * @email parakovo@gmail.com
 */
public class ProjectTest {

    // @Test
    public void updateJavaDoc() throws IOException {
        File file = new File("D:\\Java\\Learn\\kraft\\kraft-core\\target\\site\\apidocs\\allclasses-frame.html");
        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        File newFile = new File("D:\\Java\\Learn\\kraft\\allclasses-frame.html");
        if  (newFile.createNewFile()) {
            FileOutputStream outputStream = new FileOutputStream(newFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            String line;
            int n = 0;
            while ((line = bufferedReader.readLine()) != null) {
                n++;
                // 16 <= n <= 333
                if (16 <= n && n <= 333 && n % 2 == 1) {
                    continue;
                }
                bufferedWriter.write(line + "\n");
            }

            bufferedWriter.close();
            outputStreamWriter.close();
            outputStream.close();
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

    private String javaSuffix = ".java";
    private int fileCount = 0;

    @Test
    public void printJavaFileCount() {
        String projectPath = System.getProperty("user.dir");
        getDirFileCount(new File(projectPath + "/src/main/java"), javaSuffix);
        System.out.println("core java file count: " + fileCount);;
    }

    private void getDirFileCount(File dir, String suffix) {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isDirectory()) {
                    getDirFileCount(file, suffix);
                } else if (file.getName().endsWith(suffix)) {
                    // System.out.println(file.getName());
                    fileCount++;
                }
            }
        }
    }

}
