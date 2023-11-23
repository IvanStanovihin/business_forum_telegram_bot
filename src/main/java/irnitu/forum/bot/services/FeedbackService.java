package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.EducationBlock;
import irnitu.forum.bot.models.entities.Feedback;
import irnitu.forum.bot.models.entities.User;
import irnitu.forum.bot.repositories.EducationBlockRepository;
import irnitu.forum.bot.repositories.FeedbackRepository;
import irnitu.forum.bot.repositories.UserRepository;
import irnitu.forum.bot.utils.ExcelUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Service
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final UserRepository userRepository;
  private final EducationBlockRepository educationBlockRepository;

  public FeedbackService(FeedbackRepository feedbackRepository,
      UserRepository userRepository,
      EducationBlockRepository educationBlockRepository) {
    this.feedbackRepository = feedbackRepository;
    this.userRepository = userRepository;
    this.educationBlockRepository = educationBlockRepository;
  }

  /**
   * Метод для сохранения отзыва пользователя в БД.
   */
  public void addFeedback(String userTelegramName, Long educationBlockId, String message) {
    User user = userRepository.findByTelegramUserName(userTelegramName);
    EducationBlock educationBlock = educationBlockRepository.findById(educationBlockId).get();
    Feedback feedback = new Feedback()
        .setMessage(message)
        .setEducationBlock(educationBlock)
        .setStudent(user);
    feedbackRepository.save(feedback);
  }

  /**
   * Метод для генерации excel файла, в котором собрана информация со всеми отзывами участников
   */
  public InputFile getAllFeedbacks() throws IOException {
      Workbook workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("Отзывы");

      Row header = sheet.createRow(0);

      ExcelUtil excelUtil = new ExcelUtil();
      CellStyle headerStyle = excelUtil.getHeaderStyle(workbook);
      CellStyle textStyle = excelUtil.getTextStyle(workbook);

      Cell headerCell = header.createCell(0);
      headerCell.setCellValue("Данные участника");
      headerCell.setCellStyle(headerStyle);

      headerCell = header.createCell(1);
      headerCell.setCellValue("Telegram участника");
      headerCell.setCellStyle(headerStyle);

      headerCell = header.createCell(2);
      headerCell.setCellValue("Блок мероприятий");
      headerCell.setCellStyle(headerStyle);

      headerCell = header.createCell(3);
      headerCell.setCellValue("Отзыв");
      headerCell.setCellStyle(headerStyle);

      //Заполняем excel файл всеми данными из таблицы feedbacks
      List<Feedback> allFeedbacks = feedbackRepository.findAll();
      for(int i = 0; i < allFeedbacks.size(); i++){
          Row informationRow = sheet.createRow(i + 1);
          String userRegistrationInfo = allFeedbacks.get(i).getStudent().getRegistrationInformation();
          Cell informationCell = informationRow.createCell(0);
          informationCell.setCellStyle(textStyle);
          informationCell.setCellValue(userRegistrationInfo);

          String userTelegramName = allFeedbacks.get(i).getStudent().getTelegramUserName();
          informationCell = informationRow.createCell(1);
          informationCell.setCellStyle(textStyle);
          informationCell.setCellValue(userTelegramName);

          String educationBlock = allFeedbacks.get(i).getEducationBlock().getName();
          informationCell = informationRow.createCell(2);
          informationCell.setCellStyle(textStyle);
          informationCell.setCellValue(educationBlock);

          String feedbackMessage = allFeedbacks.get(i).getMessage();
          informationCell = informationRow.createCell(3);
          informationCell.setCellStyle(textStyle);
          informationCell.setCellValue(feedbackMessage);
      }

        File currentDir = new File(".");
        String path = currentDir.getAbsolutePath();
        String currentDateTime = LocalDateTime.now().toString().replaceAll(":", "_").replaceAll("/.", "_");
        String fileLocation = path.substring(0, path.length() - 1) + "feedbacks" + currentDateTime + ".xlsx";
        FileOutputStream fos = new FileOutputStream(fileLocation);
        workbook.write(fos);
        workbook.close();
        InputFile inputFile = new InputFile();
        return inputFile.setMedia(new File(fileLocation));
  }
}
