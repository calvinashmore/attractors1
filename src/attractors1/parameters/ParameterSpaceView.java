/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.ArrayParams;
import attractors1.math.Generator;
import attractors1.math.Point3d;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * view that includes a ParameterSpaceRenderer +
 */
public class ParameterSpaceView extends JPanel {

  private final ParameterSpaceRendererPanel rendererPanel;
  private CaptionedEdit maxY, maxX, minY, minX, indexY, indexX;
  private Generator<Point3d, ArrayParams> currentGenerator;
  private ArrayParams currentBaseParams;

  private final JButton updateViewButton;

  public ParameterSpaceView(ParameterSpaceRendererPanel.ParamListener paramListener) {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    rendererPanel = new ParameterSpaceRendererPanel(paramListener);
    add(rendererPanel, c);

    c.weightx = 0;
    c.weighty = 0;
    c.fill = GridBagConstraints.NONE;

    c.gridx = 0;
    c.gridy = 1;
    updateViewButton = new JButton("update");
    updateViewButton.setEnabled(false);
    updateViewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateView();
      }
    });
    add(updateViewButton, c);

    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.VERTICAL;
    add(buildLeftControlBar(), c);

    c.gridx = 1;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    add(buildRightControlBar(), c);
  }

  private ActionListener navigateAction(final double dMinX, final double dMaxX, final double dMinY, final double dMaxY) {
    return new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {
        ParameterViewParameters viewParams = getUiViewParams();
        double dX = viewParams.maxXParam - viewParams.minXParam;
        double dY = viewParams.maxYParam - viewParams.minYParam;
        ParameterViewParameters newParams = new ParameterViewParameters(
                viewParams.minXParam + dX*dMinX,
                viewParams.maxXParam + dX*dMaxX,
                viewParams.minYParam + dY*dMinY,
                viewParams.maxYParam + dY*dMaxY,
                viewParams.indexXParam,
                viewParams.indexYParam);
        refreshViewParams(newParams);
      }
    };
  }

  private JButton createButton(String text, ActionListener listener) {
    JButton button = new JButton(text);
    button.addActionListener(listener);
    return button;
  }

  private JPanel buildLeftControlBar() {
    JPanel bar = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridy = 0;

    JPanel topControls = new JPanel();
    topControls.setLayout(new BoxLayout(topControls, BoxLayout.Y_AXIS));
    topControls.add(maxY = new CaptionedEdit("max y"));
    topControls.add(createButton("^", navigateAction(0, 0, -.5, -.5)));
    bar.add(topControls, c);

    c.gridy = 4;
    JPanel bottomControls = new JPanel();
    bottomControls.setLayout(new BoxLayout(bottomControls, BoxLayout.Y_AXIS));
    bottomControls.add(createButton("v", navigateAction(0, 0, .5, .5)));
    bottomControls.add(minY = new CaptionedEdit("min y"));
    bar.add(bottomControls, c);

    c.gridy = 2;
    JPanel middleControls = new JPanel();
    middleControls.setLayout(new BoxLayout(middleControls, BoxLayout.Y_AXIS));
    middleControls.add(createButton("+", navigateAction(0, 0, .25, -.25)));
    middleControls.add(indexY = new CaptionedEdit("index y"));
    middleControls.add(createButton("-", navigateAction(0, 0, -.5, .5)));
    bar.add(middleControls, c);

    c.gridy = 1;
    c.weighty = 1;
    bar.add(new JLabel(), c);

    c.gridy = 3;
    bar.add(new JLabel(), c);

    return bar;
  }

  private JPanel buildRightControlBar() {
    JPanel bar = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 4;
    JPanel rightControls = new JPanel();
    rightControls.setLayout(new BoxLayout(rightControls, BoxLayout.X_AXIS));
    rightControls.add(maxX = new CaptionedEdit("max x"));
    rightControls.add(createButton(">", navigateAction(.5, .5, 0, 0)));
    bar.add(rightControls, c);

    c.gridx = 0;
    JPanel leftControls = new JPanel();
    leftControls.setLayout(new BoxLayout(leftControls, BoxLayout.X_AXIS));
    leftControls.add(minX = new CaptionedEdit("min x"));
    leftControls.add(createButton("<", navigateAction(-.5, -.5, 0, 0)));
    bar.add(leftControls, c);

    c.gridx = 2;
    JPanel middleControls = new JPanel();
    middleControls.setLayout(new BoxLayout(middleControls, BoxLayout.X_AXIS));
    middleControls.add(createButton("-", navigateAction(-.5, .5, 0, 0)));
    middleControls.add(indexX = new CaptionedEdit("index x"));
    middleControls.add(createButton("+", navigateAction(.25, -.25, 0, 0)));
    bar.add(middleControls, c);

    c.gridx = 1;
    c.weightx = 1;
    bar.add(new JLabel(), c);

    c.gridx = 3;
    bar.add(new JLabel(), c);

    return bar;
  }

  private void updateView() {
    if(currentGenerator == null)
      return;
    rendererPanel.setDisplay(currentGenerator, currentBaseParams, getUiViewParams());
  }

  /** Returns view params from the current UI. */
  private ParameterViewParameters getUiViewParams() {
    return new ParameterViewParameters(
            Double.parseDouble(minX.getText()),
            Double.parseDouble(maxX.getText()),
            Double.parseDouble(minY.getText()),
            Double.parseDouble(maxY.getText()),
            Integer.parseInt(indexX.getText()),
            Integer.parseInt(indexY.getText()));
  }

  /** refresh the UI elements with the current params */
  private void refreshViewParams(ParameterViewParameters viewParams) {
    minX.setText(""+viewParams.minXParam);
    maxX.setText(""+viewParams.maxXParam);
    minY.setText(""+viewParams.minYParam);
    maxY.setText(""+viewParams.maxYParam);
    indexX.setText(""+viewParams.indexXParam);
    indexY.setText(""+viewParams.indexYParam);
  }

  private ParameterViewParameters newViewParams(ArrayParams params) {
    int ix = 0;
    int iy = 1;
    double viewSize = 1.0;
    return new ParameterViewParameters(
            params.getData()[ix] - viewSize,
            params.getData()[ix] + viewSize,
            params.getData()[iy] - viewSize,
            params.getData()[iy] + viewSize,
            ix, iy);
  }

  public void setDisplay(Generator<Point3d, ArrayParams> generator, ArrayParams baseParams) {
    this.currentGenerator = generator;
    this.currentBaseParams = baseParams;
    updateViewButton.setEnabled(true);

    ParameterViewParameters viewParams = newViewParams(baseParams);
    refreshViewParams(viewParams);
    rendererPanel.setDisplay(generator, baseParams, viewParams);
  }

  public void stopCalculation() {
    rendererPanel.stopCalculation();
  }

  private class CaptionedEdit extends JPanel {
    private final JTextField field;

    public CaptionedEdit(String caption) {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      add(new JLabel(caption));
      add(field = new JTextField());
    }

    public String getText() {
      return field.getText();
    }

    public void setText(String text) {
      field.setText(text);
    }
  }
}
