package entity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Akasaka Isami
 * @description 方法实体
 * @date 2022-12-13 15:07:59
 */
public class Function {
    private static final Logger logger = LoggerFactory.getLogger(Function.class);

    private MethodDeclaration node;
    private String id; /** 项目名_文件名_函数+参数 构成该方法的唯一标识 */

    private String fileName;
    private String functionName;
    private Map<String, String> parameters;

    private int beginRowNum;
    private int endRowNum;

    private List<MyStatement> statements;

    private List<Function> parents;
    private List<Function> children;

    Function(String _fileName, Node node) {
        if (!(node instanceof MethodDeclaration)) {
            logger.error("Function constructor: 输入非MethodDeclaration节点");
            return;
        }

        MethodDeclaration methodDeclaration = ((MethodDeclaration) node).asMethodDeclaration();
        String _id = _fileName + "_" + methodDeclaration.getDeclarationAsString(false, false, true);
        logger.info("Function constructor: 正在创建的方法 " + _id);

        this.node = methodDeclaration;
        this.id = _id;
    }

}
