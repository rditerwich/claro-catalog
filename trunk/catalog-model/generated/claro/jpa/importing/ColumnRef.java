package claro.jpa.importing;


public class ColumnRef extends ImportingExpression {
    private String columnName;
    private Integer columnNumber;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String value) {
        this.columnName = value;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer value) {
        this.columnNumber = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ColumnRef) {
            ColumnRef otherColumnRef = (ColumnRef) other;
            return super.equals(otherColumnRef);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}