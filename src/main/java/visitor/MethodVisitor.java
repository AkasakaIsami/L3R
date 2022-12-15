package visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.CFGBuilder;

/**
 * @author Akasaka Isami
 * @description 单个文件的函数遍历器 遍历函数的同时为每个statement构造AST
 * @date 2022-12-13 21:22:30
 */
public class MethodVisitor extends VoidVisitorAdapter<Void> {
    private static final Logger logger = LoggerFactory.getLogger(MethodVisitor.class);

    @Override
    public void visit(MethodDeclaration node, Void arg) {
        super.visit(node, arg);

        if (node.getType() == null || !node.getParentNode().isPresent()) {
            logger.warn("");
            return;
        }

        String methodName = node.getDeclarationAsString(true, false, true);
        logger.info("MethodVisitor: 正在处理的方法为" + methodName);

        if (!(node.getParentNode().get() instanceof TypeDeclaration)) {
            logger.warn("MethodVisitor: 匿名对象的方法不处理");
            return;
        }

        CFGBuilder.buildMethodCFG(node, "");


    }
}