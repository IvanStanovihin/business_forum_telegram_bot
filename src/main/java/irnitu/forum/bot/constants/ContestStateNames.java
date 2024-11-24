package irnitu.forum.bot.constants;

public enum ContestStateNames {

    ENTERING_A_WORDS("Фаза ввода слов"),
    ENTERING_A_PHRASE("Фаза ввода фразы");

    private String name;

    private ContestStateNames(String name){
        this.name = name;
    }

    private String getName(){
        return name;
    }
}
