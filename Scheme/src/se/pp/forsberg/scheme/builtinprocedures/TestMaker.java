package se.pp.forsberg.scheme.builtinprocedures;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestMaker extends JFrame {
  private static final long serialVersionUID = 1L;
  JTextArea input, output;
  
  
  TestMaker() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    init();
  }
  void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JComponent toolbar = new JPanel();
    toolbar.setLayout(new FlowLayout());
    JButton convert = new JButton("Convert");
    convert.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { convert_actionPerformer(e); }});
    toolbar.add(convert);
    getContentPane().add(toolbar, BorderLayout.NORTH);
    
    input = new JTextArea(25, 80);
    JComponent top = new JScrollPane(input);
    top.setBorder(BorderFactory.createTitledBorder("Test cases (from r7rs)"));
    
    output = new JTextArea(25, 80);
    JComponent bottom = new JScrollPane(output);
    bottom.setBorder(BorderFactory.createTitledBorder("JUnit code"));
    
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
    getContentPane().add(split, BorderLayout.CENTER);
    
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }
  
  
  protected void convert_actionPerformer(ActionEvent e) {
    output.setText(convert(input.getText().split("\n")));
  }
  
  private String convert(String[] cases) {
    String result = "";
    for (String testCase: cases) {
      result += convert(testCase);
    }
    return "  @Test\n" +
           "  public void test() throws SchemeException {\n" +
           result +
           "  }\n";
  }
  private String convert(String testCase) {
    String[] pieces = testCase.split(" =\\) ");
    if (pieces.length < 2) return "";
    String test = pieces[0];
    String result = pieces[1];
    result = result.replace("\\", "\\\\");
    result = result.replace("\"", "\\\"");
    test = test.replace("\\", "\\\\");
    test = test.replace("\"", "\\\"");
    
    return "    assertEquals(eval(\"" + result + "\"), eval(\"" + test + "\"));\n";
  }
  public static void main(String[] args) {
    try {
      new TestMaker();
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

}
