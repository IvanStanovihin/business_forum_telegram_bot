package irnitu.forum.bot.constants;

public enum BotStateNames {

    WAIT_REGISTRATION("ожидание ввода данных для регистрации"),
    WAIT_FEEDBACK("ожидание отзыва"),
    WAIT_WORD("ожидание слова"),
    WAIT_PHRASE("ожидание фразы"),
    EMPTY_STATE("нет состояния");

    private String name;

    private BotStateNames(String name){
        this.name = name;
    }

    private String getName(){
        return name;
    }
}
