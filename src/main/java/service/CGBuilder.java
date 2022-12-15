package service;

import com.github.javaparser.ast.Node;
import entity.Function;
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
public class CGBuilder {
    private static final Logger logger = LoggerFactory.getLogger(CGBuilder.class);

    private List<Function> functions;
    private Map<String, Function> functionMap;

    public CGBuilder() {
        functions = new ArrayList<>();
        functionMap = new HashMap<>();
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, Function> functionMap) {
        this.functionMap = functionMap;
    }



    public void buildCG(Node node) {

    }

}
