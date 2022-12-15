package entity;

/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-14 20:33:11
 */
public enum StatementType {
    METHOD_DECLARATION(0),
    EXPRESSION(1),
    IF(2),
    WHILE(3),
    FOR(4),
    FOREACH(5),
    SWITCH(6),
    DO(7),
    BREAK(8),
    CONTINUED(9),
    LABELED(10),
    RETURN(11),
    EMPTY(12),
    ASSERT(13),
    THROW(14);

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
