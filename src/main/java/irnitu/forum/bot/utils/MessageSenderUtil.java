package irnitu.forum.bot.utils;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Slf4j
public class MessageSenderUtil {

  /**
   * Метод для разрезания длинных сообщений на маленькие части. Из за ограничений телеграмма
   * большие текстовые сообщения не отправляются.
   */
  public static List<SendMessage> splitMessage(List<SendMessage> messages){
    List<SendMessage> splitMessages = new LinkedList<>();
    for (SendMessage message : messages){
      List<String> messageTexts = splitString(message.getText());
      List<SendMessage> sendMessages =  textToSendMessage(messageTexts, message.getChatId(), message.getReplyMarkup());
      splitMessages.addAll(sendMessages);
    }
    return splitMessages;
  }

  /**
   * В методе длинная строка нарезается на меньшие куски, по 4096 символов. Нарезанные куски
   * перекладываются в список для дальнейшей обработки.
   */
  public static List<String> splitString(String text){
    int splitOffset = 0;
    List<String> splitStrings = new LinkedList<>();
    while(true){
      if (text.length() > 4096){
        splitStrings.add(text.substring(splitOffset, splitOffset + 4096));
        splitOffset += 4096;
        text = text.substring(splitOffset);
      } else{
        splitStrings.add(text);
        break;
      }
    }
    log.info("Split strings: {}", splitStrings);
    return splitStrings;
  }

  private static List<SendMessage> textToSendMessage(List<String> texts, String chatId, ReplyKeyboard markup){
    List<SendMessage> messages = new LinkedList<>();
    for (String text : texts){
      SendMessage sendMessage = new SendMessage();
      sendMessage.setText(text);
      sendMessage.setChatId(chatId);
      if (markup != null){
        sendMessage.setReplyMarkup(markup);
      }
      sendMessage.setParseMode(ParseMode.HTML);
      messages.add(sendMessage);
    }
    return messages;
  }
}
