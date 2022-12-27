package service;

import com.github.javaparser.ast.Node;
import entity.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Akasaka Isami
 * @description 函数调用图的构造器
 * @date 2022-12-13 14:16:29
 */
public class CGConstructor {
    private static final Logger logger = LoggerFactory.getLogger(CGConstructor.class);

    private List<Method> functions;
    private Map<String, Method> functionMap;

    public CGConstructor() {
        functions = new ArrayList<>();
        functionMap = new HashMap<>();
    }

    public List<Method> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Method> functions) {
        this.functions = functions;
    }

    public Map<String, Method> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, Method> functionMap) {
        this.functionMap = functionMap;
    }



    public void buildCG(Node node) {

    }

}
