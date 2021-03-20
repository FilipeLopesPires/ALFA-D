package Gramatica;
public enum Type{
	TABLE{

                    @Override
                    public String toString() {
                        return "TABLE";
                    }
                }, 
	COLUMN{

                    @Override
                    public String toString() {
                        return "COLUMN";
                    }
                }, 
	LINE{

                    @Override
                    public String toString() {
                        return "LINE";
                    }
                }, 
	BOOL{

                    @Override
                    public String toString() {
                        return "BOOL";
                    }
                }, 
	INT{

                    @Override
                    public String toString() {
                        return "INT";
                    }
                }, 
	REAL{

                    @Override
                    public String toString() {
                        return "REAL";
                    }
                }, 
	VOID{

                    @Override
                    public String toString() {
                        return "VOID";
                    }
                }, 
	STRING{

                    @Override
                    public String toString() {
                        return "STRING";
                    }
                }
}
