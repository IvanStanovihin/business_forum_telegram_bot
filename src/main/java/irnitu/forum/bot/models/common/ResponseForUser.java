package irnitu.forum.bot.models.common;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Data
public class ResponseForUser {

  private SendMessage sendMessage;
  private SendDocument sendDocument;

  public ResponseForUser(SendMessage sendMessage){
    this.sendMessage = sendMessage;
  }

  public ResponseForUser(SendDocument sendDocument){
    this.sendDocument = sendDocument;
  }

  public ResponseForUser(SendMessage sendMessage, SendDocument sendDocument){
    this.sendMessage = sendMessage;
    this.sendDocument = sendDocument;
  }
}
