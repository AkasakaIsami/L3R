import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import visitor.MethodVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Akasaka Isami
 * @description 代码处理的入口 take the directory of the project as input
 * @date 2022-12-13 17:10:28
 */
public class Entry {
    private static final Logger logger = LoggerFactory.getLogger(Entry.class);
    private static final String root = "data/processed/";
    private static final String projectName = "zookeeper_demo";


    public static void main(String[] args) throws FileNotFoundException {


        String srcDirPath = root + projectName;
        File srcDir = new File(srcDirPath);
        if (!srcDir.isDirectory()) {
            return;
        }

        // 遍历所有文件
        for (File file : Objects.requireNonNull(srcDir.listFiles())) {
            String fileName = file.getName();

            logger.info("Entry: 正在解析文件" + fileName);

            CompilationUnit cu = JavaParser.parse(file);

            VoidVisitor<String> methodVisitor = new MethodVisitor();
            methodVisitor.visit(cu, fileName);
        }

    }


}
