package converter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter
{
	public int keyLeft;
	public int keyRight;
	public int keyUp;
	public int keyDown;
	public int keyEnter;

	KeyInput()
	{
		keyLeft = 0;
		keyRight = 0;
		keyUp = 0;
		keyDown = 0;
		keyEnter = 0;
	}

	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_LEFT:
				keyLeft = (keyLeft == 0) ? 1 : 2;
				break;

			case KeyEvent.VK_RIGHT:
				keyRight = (keyRight == 0) ? 1 : 2;
				break;

			case KeyEvent.VK_UP:
				keyUp = (keyUp == 0) ? 1 : 2;
				break;

			case KeyEvent.VK_DOWN:
				keyDown = (keyDown == 0) ? 1 : 2;
				break;

			case KeyEvent.VK_ENTER:
				keyEnter = (keyEnter == 0) ? 1 : 2;
				break;

			default:
		}
	}

	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_LEFT:
				keyLeft = 0;
				break;

			case KeyEvent.VK_RIGHT:
				keyRight = 0;
				break;

			case KeyEvent.VK_UP:
				keyUp = 0;
				break;

			case KeyEvent.VK_DOWN:
				keyDown = 0;
				break;

			case KeyEvent.VK_ENTER:
				keyEnter = 0;
				break;

			default:
		}
	}
}