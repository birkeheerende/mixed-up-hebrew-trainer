package vokabeltrainer.tonionlayout;

/*
 * Copyright (c) 2023, Birke Heeren All rights reserved.
 * Original name: tonionlayout in package de.copepod
 * Use only at own risk.
 *
 * swing-easy project
 * Version 1.0: 2023-11-11
 */
import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

import javax.swing.JViewport;

/**
 * The <code>BullsEyeLayout</code> class is a layout manager that lays out a
 * container's component in the center.
 * <p>
 * Minimum and maximum sizes are taken into account.
 * <p>
 * <code>TotemLayout</code>, <code>TrainLayout</code> and
 * <code>BullsEyeLayout</code> work together like layers of an onion. They stack
 * into each other and are called swing-easy-layout. swing-easy-layout was developed to
 * layout forms and data masks. By using minimum and maximum size the layout
 * will resize to fit the available space. The components inside swing-easy-layout
 * only have to fit together approximately, the layout will align the components
 * to look neatly by itself. 
 * <p>
 * <code>BullsEyeLayout</code> will give the component the maximal possible width and height.
 * <p>
 * Even though swing-easy-layout is done top-down, each layer inquires about the
 * minimum and maximum sizes of all its components. To acquire a good
 * performance each layer caches the overall minimum and maximum size of its
 * components. Therefore swing-easy-layout can not be shared. Adding or removing a
 * component invalidates the cache of the layout and all swing-easy-layout above it.
 * <p>
 * All first components inside a swing-easy-layout must have a minimum and maximum
 * size set for the layout to function properly, otherwise minimum and maximum
 * sizes are estimated. swing-easy-layout layers that change between filled and empty should
 * have a minimum and maximum size set, which is only used when empty.
 * <p>
 * JButtons should be wrapped with a JPanel that has a FlowLayout. The minimum
 * and maximum sizes are set on the JPanel.
 * <p>
 * JTables should be wrapped with a JPanel that has a BorderLayout and be added
 * to the center component. The minimum and maximum sizes are set on the JPanel.
 * <p>
 * swing-easy-layout can be placed inside a JScrollPane. If the window size is
 * deceased swing-easy-layout will shrink to its minimum size before the scroll bars
 * appear.
 * <p>
 * swing-easy-layout corrects inconsistencies of minimum and maximum sizes with
 * maximum = minimum;
 *
 * @author Birke Heeren
 * @version BullsEyeLayout 1.0
 */

public class BullsEyeLayout
      implements LayoutManager, LayoutManager2, java.io.Serializable
{

   /*
    * serialVersionUID
    */
   private static final long serialVersionUID = 5350471242829162225L;

   /**
    * BullsEyeLayout remembers the minimum size of its components. Adding or
    * deleting a component causes the minimum size to be recalculated. The
    * update is passed up the swing-easy-layout layers to the outside, therefore
    * BullsEyeLayout must know the component it is assigned to. BullsEyeLayout
    * can not be shared between components.
    */
   private Dimension dimMin;

   /**
    * This is the container BullsEyeLayout is assigned to.
    */
   private Container self;

   /**
    * This is a name for test mode.
    */
   private String testname;

   /**
    * This determines test mode or not.
    */
   private LayoutMode mode;

   /**
    * Creates a BullsEyeLayout, no test.
    * 
    * @param self
    *           the container to be laid out
    */
   public BullsEyeLayout(Container self)
   {
      this(self, "", LayoutMode.NOTEST);
   }

   /**
    * Creates a BullsEyeLayout in test mode.
    * <p>
    * 
    * @param self
    *           the container to be laid out
    * @param testname
    *           the name of the object in test mode
    */
   public BullsEyeLayout(Container self, String testname)
   {
      this(self, testname, LayoutMode.TEST);
   }

   /**
    * private constructor
    * <p>
    *
    * All <code>BullsEyeLayout</code> constructors defer to this one.
    */
   private BullsEyeLayout(Container self, String testname,
         LayoutMode mode)
   {
      this.dimMin = null;
      this.self = self;
      this.testname = testname;
      this.mode = mode;
   }

   /**
    * Determines the preferred size of the container argument using this
    * BullsEyeLayout.
    * <p>
    * The preferred size is all size available.
    *
    * @param self
    *           the container in which to do the layout
    * @return the preferred dimensions to lay out the subcomponents of the
    *         specified container
    * @see java.awt.Container#getPreferredSize()
    */
   @Override
   public Dimension preferredLayoutSize(Container self)
   {
      synchronized (self.getTreeLock())
      {
         checkContainer(self);
         int ncomponents = self.getComponentCount();
         if (ncomponents > 1)
         {
            throw new AWTError("ExpanderLayout can hold only one component");
         }
         if (ncomponents == 0)
         {
            if (self.getMinimumSize() != null)
            {
               return self.getMinimumSize();
            }
            else if (self.getParent() instanceof JViewport)
            {
               JViewport vp = (JViewport) self.getParent();
               Insets insets = self.getInsets();
               return new Dimension(vp.getWidth() - insets.left - insets.right,
                     vp.getHeight() - insets.top - insets.bottom);
            }
            else
            {
               return self.getSize();
            }
         }

         return this.maximumLayoutSize(self);
      }
   }

   /**
    * Determines the minimum size of the container argument using this
    * BullsEyeLayout.
    * <p>
    * The minimum height of a BullsEyeLayout is the minimum height of the
    * component in the container, plus the top and bottom insets of the self
    * container.
    * <p>
    * The minimum width of a BullsEyeLayout is the minimum width of the
    * component in the container, plus the left and right insets of the self
    * container.
    *
    * @param self
    *           the container in which to do the layout
    * @return the minimum dimensions needed to lay out the subcomponents of the
    *         specified container
    * @see java.awt.Container#doLayout
    */
   @Override
   public Dimension minimumLayoutSize(Container self)
   {
      synchronized (self.getTreeLock())
      {
         checkContainer(self);
         int ncomponents = self.getComponentCount();
         if (ncomponents == 0)
         {
            dimMin = null;
            return null;
         }
         if (ncomponents > 1)
         {
            throw new AWTError("BullsEyeLayout can hold only one component");
         }
         if (dimMin != null)
            return dimMin;
         Insets insets = self.getInsets();
         int h = 0;
         int w = 0;
         ncomponents = self.getComponentCount();
         if (ncomponents > 1)
         {
            throw new AWTError("BullsEyeLayout can hold only one component");
         }

         Dimension dmin = null;
         if (ncomponents == 0)
         {
            dmin = self.getMinimumSize();
         }
         else
         {
            Component comp = self.getComponent(0);

            /*
             * In case Component is Container with Layout instance of
             * TrainLayout, TotemLayout or BullsEyeLyout the dimensions derived
             * by content - if any - should override given Dimensions. Only when
             * there is no content the given Dimensions should be used.
             */
            if (comp instanceof Container && (((Container) comp)
                  .getLayout() instanceof TotemLayout
                  || ((Container) comp).getLayout() instanceof TrainLayout
                  || ((Container) comp).getLayout() instanceof BullsEyeLayout))
            {
               Dimension dminContent = ((LayoutManager2) ((Container) comp)
                     .getLayout()).minimumLayoutSize((Container) comp);
               if (dminContent != null)
                  dmin = dminContent;
               else
                  dmin = comp.getMinimumSize();
            }
            else
            {
               dmin = comp.getMinimumSize();
            }
         }

         if (dmin != null)
         {
            if (h < dmin.height)
               h = dmin.height; // minheight is maximized
            if (w < dmin.width)
               w = dmin.width; // minwidth is maximized
         }
         else
         {
            h = self.getHeight() - (insets.top + insets.bottom);
            w = self.getWidth() - (insets.left + insets.right);
         }

         dimMin = new Dimension(w, h);
         return dimMin;
      }
   }

   /**
    * Determines the maximum size of the container argument using this
    * BullsEyeLayout.
    * <p>
    * The maximum height of a BullsEyeLayout is the maximum height 
    * available, but at least the minimum size of self.
    * <p>
    * The maximum width of a BullsEyeLayout is the maximum width available,
    * but at least the minimum width of self
    *
    * @param self
    *           the container in which to do the layout
    * @return the maximum dimensions needed to lay out the subcomponents of the
    *         specified container
    * @see java.awt.Container#doLayout
    */
   @Override
   public Dimension maximumLayoutSize(Container self)
   {
      synchronized (self.getTreeLock())
      {
         checkContainer(self);
         int ncomponents = self.getComponentCount();
         if (ncomponents == 0)
         {
            dimMin = null;
            return null;
         }
         if (ncomponents > 1)
         {
            throw new AWTError("BullsEyeLayout can hold only one component");
         }
         double h = self.getSize().getHeight()
               - (self.getInsets().top + self.getInsets().bottom);
         double w = self.getSize().getWidth()
               - (self.getInsets().left + self.getInsets().right);
         
         
         if(self.getMinimumSize() != null)
         {
            double hmin = self.getMinimumSize().height;
            double wmin = self.getMinimumSize().width;
            if(h < hmin)
            {
               h = hmin;
            }
            if(w < wmin)
            {
               w = wmin;
            }
         }
         return new Dimension((int)w, (int)h);
      }
   }

   /**
    * Lays out the specified container using this layout.
    * <p>
    * This method reshapes the component in the specified self container in
    * order to satisfy the constraints of the <code>BullsEyeLayout</code>
    * object.
    * <p>
    * The component in a BullsEyeLayout is given the maximal height and width
    * within its minimum and maximum dimensions range. If the available space is
    * larger than needed by the component, then the component is placed in the
    * center. If the available space is smaller than needed by the component,
    * then the component is placed at the top respectively left and some part of
    * it will be hidden.
    *
    * @param self
    *           the container in which to do the layout
    * @see java.awt.Container
    * @see java.awt.Container#doLayout
    */
   @Override
   public void layoutContainer(Container self)
   {
      synchronized (self.getTreeLock())
      {
         checkContainer(self);
         int ncomponents = self.getComponentCount();
         if (ncomponents == 0)
         {
            dimMin = null;
            return;
         }
         if (ncomponents > 1)
         {
            throw new AWTError("BullsEyeLayout can hold only one component");
         }
         ncomponents = self.getComponentCount();
         if (ncomponents > 1)
         {
            throw new AWTError("BullsEyeLayout can hold only one component");
         }
         if (ncomponents == 0)
            return;

         Insets insets = self.getInsets();
         int availableHeight = maximumLayoutSize(self).height;
         int availableWidth = maximumLayoutSize(self).width;

         int h = availableHeight;
         int w = availableWidth;
         int hmin = 0;
         int hmax = Integer.MAX_VALUE;
         int wmin = 0;
         int wmax = Integer.MAX_VALUE;

         Component comp = self.getComponent(0);
         Dimension dmin;
         Dimension dmax;
         /*
          * In case Component is Container with Layout instance of TrainLayout
          * or TotemLayout the dimensions derived by content - if any - should
          * override given Dimensions. Only when there is no content the given
          * Dimensions should be used.
          */
         if (comp instanceof Container && (((Container) comp)
               .getLayout() instanceof TotemLayout
               || ((Container) comp).getLayout() instanceof TrainLayout))
         {
            Dimension dminContent = ((LayoutManager2) ((Container) comp)
                  .getLayout()).minimumLayoutSize((Container) comp);
            if (dminContent != null)
               dmin = dminContent;
            else
               dmin = comp.getMinimumSize();

            Dimension dmaxContent = ((LayoutManager2) ((Container) comp)
                  .getLayout()).maximumLayoutSize((Container) comp);
            if (dmaxContent != null)
               dmax = dmaxContent;
            else
               dmax = comp.getMaximumSize();
         }
         else if (((Container) comp).getLayout() instanceof BullsEyeLayout)
         {
            Dimension dminContent = ((LayoutManager2) ((Container) comp)
                  .getLayout()).minimumLayoutSize((Container) comp);
            if (dminContent != null)
               dmin = dminContent;
            else
               dmin = comp.getMinimumSize();
            dmax = comp.getMaximumSize();
         }
         else
         {
            dmin = comp.getMinimumSize();
            dmax = comp.getMaximumSize();
         }

         // MINIMUM
         if (dmin != null)
         {
            if (dmin.height > hmin)
               hmin = dmin.height; // minheight is maximized
            if (dmin.width > wmin)
               wmin = dmin.width; // minwidth is maximized
         }
         else // minimum was not set on innermost layer
         {
            // w = w;
         }

         // MAXIMUM
         if (dmax != null)
         {
            if (dmax.height < hmax)
               hmax = dmax.height; // maxheight is minimized
            if (dmax.width < wmax)
               wmax = dmax.width; // maxwidth is minimized
         }
         else // maximum was not set on innermost layer
         {
            // w = w;
         }

         // height
         if (hmin > hmax)
         {
            // error correction
            hmax = hmin;
         }

         if (hmax != Integer.MAX_VALUE)
         {
            if (h <= hmin)
               h = hmin;
            else if (hmax < h)
               h = hmax;
            // else h = h;
         }
         else if (h < hmin)
            h = hmin;
         // else h = h;

         // width
         if (wmin > wmax)
         {
            // error correction
            wmax = wmin;
         }

         if (wmax != Integer.MAX_VALUE)
         {
            if (w <= wmin)
               w = wmin;
            else if (wmax < w)
               w = wmax;
            // else w = w;
         }
         else if (w < wmin)
            w = wmin;
         // else w = w;

         int x = insets.left;
         int y = insets.top;
         int deltaX = (availableWidth - w) / 2 + x;
         int deltaY = (availableHeight - h) / 2 + y;

         comp.setBounds(Math.max(x, deltaX), Math.max(y, deltaY), w, h);

         if (LayoutMode.TEST == this.mode)
         {
            System.out.println("");
            System.out.println(testname + " with BullsEyeLayout");
            System.out.println("available width: " + availableWidth);
            System.out.println("available height: " + availableHeight);
            System.out.println("space left: " + Math.max(x, deltaX));
            System.out.println("space top: " + Math.max(y, deltaY));
            System.out.println("component width: " + w);
            System.out.println("component height: " + h);
            System.out.println("component min width: " + wmin);
            System.out.println("component max width: " + wmax);
            System.out.println("component min height: " + hmin);
            System.out.println("component max height: " + hmax);
            System.out.println("");
         }
      }
   }

   /**
    * invalidates Layout, minimum and maximum sizes of content will be
    * recalculated
    * 
    * @param name
    *           the name of the component
    * @param comp
    *           the component to be added
    */
   @Override
   public void addLayoutComponent(String name, Component comp)
   {
      invalidateLayout(comp.getParent());
   }

   /**
    * invalidates Layout, minimum and maximum sizes of content will be
    * recalculated
    *
    * @param constraints
    *           the constraints
    * @param comp
    *           the component to be added
    */
   @Override
   public void addLayoutComponent(Component comp, Object constraints)
   {
      invalidateLayout(comp.getParent());
   }

   /**
    * invalidates Layout, minimum and maximum sizes of content will be
    * recalculated
    * 
    * @param comp
    *           the component to be removed
    */
   @Override
   public void removeLayoutComponent(Component comp)
   {
      invalidateLayout(comp.getParent());
   }

   /**
    * Invalidates the layout, indicating that if the layout manager has cached
    * information it should be discarded.
    */
   @Override
   public void invalidateLayout(Container self)
   {
      checkContainer(self);
      this.dimMin = null;
      if (self.getParent() != null && self.getParent().getLayout() != null
            && (self.getParent().getLayout() instanceof TotemLayout
                  || self.getParent().getLayout() instanceof TrainLayout
                  || self.getParent().getLayout() instanceof BullsEyeLayout))
      {
         ((LayoutManager2) self.getParent().getLayout())
               .invalidateLayout(self.getParent());
      }
   }

   /**
    * Returns the alignment along the x axis. This specifies how the component
    * would like to be aligned relative to other components. The value should be
    * a number between 0 and 1 where 0 represents alignment along the origin, 1
    * is aligned the farthest away from the origin, 0.5 is centered, etc.
    */
   @Override
   public float getLayoutAlignmentX(Container self)
   {
      return 0;
   }

   /**
    * Returns the alignment along the y axis. This specifies how the component
    * would like to be aligned relative to other components. The value should be
    * a number between 0 and 1 where 0 represents alignment along the origin, 1
    * is aligned the farthest away from the origin, 0.5 is centered, etc.
    */
   @Override
   public float getLayoutAlignmentY(Container self)
   {
      return 0;
   }

   private void checkContainer(Container self)
   {
      if (this.self != self)
      {
         throw new AWTError("BullsEyeLayout can't be shared: " + this.testname);
      }
   }
}
