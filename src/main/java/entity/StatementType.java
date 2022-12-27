package entity;

/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-14 20:33:11
 */
public enum StatementType {

    METHOD_DECLARATION(0),

    ASSERT(1),
    BLOCK(2),
    BREAK(3),
    CONTINUE(4),
    DO(5),

    EMPTY(6),
    EXPLICIT_CONSTRUCTOR_INVOCATION(7),
    EXPRESSION(8),
    FOREACH(9),
    FOR(10),

    IF(11),
    LABELED(12),
    LOCAL_CLASS_DECLARATION(13),
    RETURN(14),
    SWTICHENTRY(15),

    SWTICH(16),
    SYNCHRONIZED(17),
    THROW(18),
    TRY(19),
    UNPARSABLE(20),

    WHILE(21),

    BLANK(22),
    ELSE(23);


    private int id;

    StatementType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
