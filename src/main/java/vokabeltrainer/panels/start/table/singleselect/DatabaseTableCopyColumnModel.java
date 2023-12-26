package vokabeltrainer.panels.start.table.singleselect;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class DatabaseTableCopyColumnModel extends DefaultTableColumnModel
{

   private static final long serialVersionUID = -126413736438939824L;

   private DatabaseTableCopyCellRenderer renderer;

   public DatabaseTableCopyColumnModel(int totalWidth)
   {
      renderer = new DatabaseTableCopyCellRenderer();
      int width = (totalWidth - 43) / 3;

      TableColumn column1 = new TableColumn();
      column1.setHeaderValue("Datenbank");
      column1.setCellRenderer(renderer);
      column1.setCellEditor(renderer);
      column1.setPreferredWidth(width + 43);
      addColumn(column1);

      TableColumn column2 = new TableColumn();
      column2.setHeaderValue("Autoren");
      column2.setCellRenderer(renderer);
      column2.setCellEditor(renderer);
      column2.setPreferredWidth(width);
      addColumn(column2);

      TableColumn column3 = new TableColumn();
      column3.setHeaderValue("Verlag");
      column3.setCellRenderer(renderer);
      column3.setCellEditor(renderer);
      column3.setPreferredWidth(width);
      addColumn(column3);
   }

}
