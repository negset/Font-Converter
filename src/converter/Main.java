package converter;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends Frame
{
	private static final long serialVersionUID = 1L;
	public static final int WID = 300;
	public static final int HEI = 300;

	Main()
	{
		super("Font Converter");

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				System.exit(0);
			}
		});

		setVisible(true);
		Insets insets = getInsets();
		setVisible(false);
		setSize(WID + insets.left + insets.right, HEI + insets.top + insets.bottom);

		MyCanvas mc = new MyCanvas();
		add(mc);
		setVisible(true);

		mc.init();
		mc.initThread();
	}

	public static void main(String[] args)
	{
		new Main();
	}
}
