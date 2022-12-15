import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import entity.node.StatementNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import visitor.MethodVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-13 17:10:28
 */
public class Entry {
    private static final Logger logger = LoggerFactory.getLogger(Entry.class);

    Map<String, List<String>> file2methods = new HashMap<>();
    Map<String, StatementNode> method2rootNode = new HashMap<>();

    public static void main(String[] args) throws FileNotFoundException {

        String srcDirPath = "C:\\Users\\akasa\\Study\\毕业设计\\L3R\\src\\main\\java\\demo";
        File srcDir = new File(srcDirPath);

        if (!srcDir.isDirectory()) {
            return;
        }

        // 遍历所有文件
        for (File file : Objects.requireNonNull(srcDir.listFiles())) {
            List<String> methods = new ArrayList<>();
            String fileName = file.getName();

            logger.info("Entry: 正在解析文件" + fileName);

            CompilationUnit cu = JavaParser.parse(file);
            VoidVisitor<?> methodVisitor = new MethodVisitor();
            methodVisitor.visit(cu, null);
        }

    }
}
