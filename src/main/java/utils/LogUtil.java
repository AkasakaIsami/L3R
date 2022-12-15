package utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Akasaka Isami
 * @description 日志语句相关的工具类
 * <a href="https://regexr-cn.com/">参考这个网址</>
 * @date 2022-12-12 20:43:38
 */
public class LogUtil {

    private static final String regexGLOBAL = ".*?(log|trace|(system\\.out)|(system\\.err)).*?(.*?);";

    private static final Set<String> logIdentifier = new HashSet<String>() {
        {
            add("log");
            add("logger");
            add("logging");
            add("getlogger");
            add("getlog");
        }
    };


    /**
     * 检测输入的statement（AST根节点）是否为一条日志语句
     *
     * @param node
     * @return
     */
    public static boolean isLogStatement(Node node) {


        return false;
    }



    /**
     * @param curStatement
     * @return
     */
    public static boolean isLogStatement(String curStatement) {
        Pattern p = Pattern.compile("\".*\"");
        Matcher m = p.matcher(curStatement);

        if (curStatement.toLowerCase().contains("assertequals") || curStatement.toLowerCase().contains("assertfalse") || curStatement.toLowerCase().contains("asserttrue")) {
            return false;
        }

        /* if find quotes */
        if (m.find()) {
            curStatement = curStatement.replaceAll("\".*?\"", "");

            if (!isLogRelated(curStatement))
                return false;

            p = Pattern.compile("[^\"]*?\\=");
            Matcher mEqualSign = p.matcher(curStatement);

            if (mEqualSign.find())
                return false;

            p = Pattern.compile("(system\\.out)|(system.err)|(log(ger)?(\\(\\))?\\.(\\w*?)\\()|logauditevent\\(", Pattern.CASE_INSENSITIVE);
            m = p.matcher(curStatement);
            return m.find();

        } else {
            p = Pattern.compile("[^\"]*?\\=");
            Matcher mEqualSign = p.matcher(curStatement);

            if (mEqualSign.find())
                return false;

            p = Pattern.compile("(system\\.out)|(system.err)|(log(ger)?(\\(\\))?\\.(\\w*?)\\()", Pattern.CASE_INSENSITIVE);
            m = p.matcher(curStatement);
            return m.find();
        }
    }


    /**
     * @param statement
     * @return
     */
    private static boolean isLogRelated(String statement) {
        if (statement.split("\r|\n|\r\n").length >= 3) // set as 3
        {
            return false;
        } else {
            Pattern p = Pattern.compile(regexGLOBAL, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(statement);
            if (m.find()) {
                if (statement.toLowerCase().contains("system.out") || statement.toLowerCase().contains("system.err")) {
                    return true;
                }
                statement = statement.replaceAll("\"(.*?)\"", "");
                Pattern pKeyword = Pattern.compile("(login)|(dialog)|(logout)|(catalog)|logic(al)?", Pattern.CASE_INSENSITIVE);
                Pattern pTrueKeyword = Pattern.compile("loginput|logoutput", Pattern.CASE_INSENSITIVE);
                m = pKeyword.matcher(statement);
                Matcher m2 = pTrueKeyword.matcher(statement);
                return !m.find() || m2.find();
            }
        }
        return false;
    }


}
