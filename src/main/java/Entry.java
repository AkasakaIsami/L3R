import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import visitor.MethodVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-13 17:10:28
 */
public class Entry {
    private static final Logger logger = LoggerFactory.getLogger(Entry.class);


    public static void main(String[] args) throws FileNotFoundException {

        String srcDirPath = "C:\\Users\\akasa\\Study\\毕业设计\\L3R\\src\\main\\java\\utils\\FileUtil.java";
        File srcDir = new File(srcDirPath);

        if (!srcDir.isDirectory()) {
            return;
        }

        // 遍历所有文件
        for (File file : Objects.requireNonNull(srcDir.listFiles())) {
            logger.info("Entry: 正在解析文件" + file.getName());

            CompilationUnit cu = JavaParser.parse(file);
            VoidVisitor<?> methodVisitor = new MethodVisitor();
            methodVisitor.visit(cu, null);
        }

    }
}
