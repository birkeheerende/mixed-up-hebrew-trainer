package vokabeltrainer.common;

import java.awt.Container;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.StringJoiner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import vokabeltrainer.PathAndFile;
import vokabeltrainer.panels.MainView;
import vokabeltrainer.panels.translation.Translation;
import vokabeltrainer.panels.translation.Translator;

public final class Common
{
   private static MainView mainJPanel;
   private static JFrame jFrame;
   private static boolean setMainJPanelOnlyOnce = false;
   private static boolean setJFrameOnlyOnce = false;
   private static Translator translator = new Translator();
   private static Settings settings;

   private Common()
   {

   }
   
   public static MainView getMainJPanel()
   {
      return mainJPanel;
   }

   static void setMainJPanel(MainView mainJPanel)
   {
      if (!setMainJPanelOnlyOnce)
      {
         Common.mainJPanel = mainJPanel;
      }
   }

   public static JFrame getjFrame()
   {
      return jFrame;
   }

   static void setjFrame(JFrame jFrame)
   {
      if (!setJFrameOnlyOnce)
      {
         Common.jFrame = jFrame;
      }
   }

   public static Translator getTranslator()
   {
      return translator;
   }

   public static void setTranslator(Translator translator)
   {
      Common.translator = translator;
   }

   public static Settings getSettings()
   {
      return settings;
   }

   public static boolean isSchabbat()
   {
      ZonedDateTime now = ZonedDateTime.now();
      DayOfWeek day = now.getDayOfWeek();
      int hour = now.getHour();
      if(day.equals(DayOfWeek.FRIDAY) && hour > 18)
      {
         return true;
      }
      else if (day.equals(DayOfWeek.SATURDAY) && hour < 18)
      {
         return true;
      }
      
      return false;
   }
   
   public static boolean isSchabbatPossible(LocalDate date)
   {
      ZonedDateTime now = ZonedDateTime.now();
      DayOfWeek day = now.getDayOfWeek();
      if(day.equals(DayOfWeek.FRIDAY) || day.equals(DayOfWeek.SATURDAY))
      {
         return true;
      }
      
      return false;
   }
   
   public static void saveAllDataAsIs(Container fenster)
   {
      PathAndFile pathOfFolder = choosesFolderAndFileForSave(fenster);
      if (pathOfFolder != null)
      {
         new SwingWorker<Void, Void>()
         {

            @Override
            protected Void doInBackground() throws Exception
            {
               SaveExpressions saver = new SaveExpressions(pathOfFolder);
               saver.export(null, false);

               return null;
            }

         }.execute();
      }
   }
   
   public static PathAndFile choosesFolderAndFileForSave(Container fenster)
   {
      JFileChooser folderChooser = new JFileChooser(
            Settings.getExpressionPath());
      folderChooser.setAcceptAllFileFilterUsed(false);
      folderChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      int choice = folderChooser.showSaveDialog(fenster);

      if (JFileChooser.APPROVE_OPTION == choice)
      {
         String splitter = "\\" + File.separator;
         String[] foldersAndFile = folderChooser.getSelectedFile().getPath()
               .split(splitter);
         PathAndFile pathAndFile = new PathAndFile();
         StringJoiner joiner = new StringJoiner(splitter);
         for (int i = 0; i < foldersAndFile.length; i++)
         {
            if (i == foldersAndFile.length - 1)
            {
               pathAndFile.setFile(foldersAndFile[i]);
            }
            else
            {
               joiner.add(foldersAndFile[i]);
            }
         }
         pathAndFile.setPath(joiner.toString());

         if (!testIfFolderExists(pathAndFile.getPath()))
         {
            JOptionPane.showMessageDialog(fenster, translator.realisticTranslate(
                  Translation.DER_GEWAEHLTE_ORDNER_EXISTIERT_NICHT_) + "\n"
                  + pathAndFile.getPath() + "\n"
                  + translator.realisticTranslate(
                        Translation.BITTE_WAEHLEN_SIE_EINEN_EXISTIERENDEN_ORDNER_)
                  + "\n" + translator.realisticTranslate(Translation.DANKE_),
                  translator.realisticTranslate(Translation.NACHRICHT),
                  JOptionPane.CLOSED_OPTION);
            return null;
         }

         if (testIfFileExists(pathAndFile.getPathFile()))
         {
            int answer = JOptionPane.showConfirmDialog(fenster,
                  translator.realisticTranslate(
                        Translation.DIE_DATEI_EXISTIERT_SCHON)
                        + "\n"
                        + translator.realisticTranslate(
                              Translation.SOLL_SIE_UEBERSCHRIEBEN_WERDEN_),
                  translator.realisticTranslate(Translation.FRAGE),
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (JOptionPane.OK_OPTION != answer)
            {
               return null;
            }
         }

         if (testIfFileExists(pathAndFile.getPathFile() + ".zip"))
         {
            int answer = JOptionPane.showConfirmDialog(fenster,
                  translator.realisticTranslate(
                        Translation.DIE_DATEI_EXISTIERT_SCHON)
                        + "\n"
                        + translator.realisticTranslate(
                              Translation.SOLL_SIE_UEBERSCHRIEBEN_WERDEN_),
                  translator.realisticTranslate(Translation.FRAGE),
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (JOptionPane.OK_OPTION != answer)
            {
               return null;
            }
         }

         if (testIfFileExists(pathAndFile.getPathFile() + ".ZIP"))
         {
            int answer = JOptionPane.showConfirmDialog(fenster,
                  translator.realisticTranslate(
                        Translation.DIE_DATEI_EXISTIERT_SCHON)
                        + "\n"
                        + translator.realisticTranslate(
                              Translation.SOLL_SIE_UEBERSCHRIEBEN_WERDEN_),
                  translator.realisticTranslate(Translation.FRAGE),
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (JOptionPane.OK_OPTION != answer)
            {
               return null;
            }
         }
         return pathAndFile;
      }
      return null;
   }
   
   private static boolean testIfFileExists(String path)
   {
      File folder = new File(path);
      return folder.exists() && folder.isFile();
   }
   
   private static boolean testIfFolderExists(String path)
   {
      File folder = new File(path);
      return folder.exists() && folder.isDirectory();
   }
}
