package irnitu.forum.bot.utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

  public CellStyle getHeaderStyle(Workbook workbook){
    CellStyle headerStyle = workbook.createCellStyle();
    XSSFFont font = ((XSSFWorkbook) workbook).createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 12);
    font.setBold(true);
//    headerStyle.setWrapText(true);
    headerStyle.setFont(font);
    return headerStyle;
  }

  public CellStyle getTextStyle(Workbook workbook){
    CellStyle textStyle = workbook.createCellStyle();
    XSSFFont font = ((XSSFWorkbook) workbook).createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 10);
    font.setBold(false);
//    textStyle.setWrapText(true);
    textStyle.setFont(font);
    return textStyle;
  }

}
