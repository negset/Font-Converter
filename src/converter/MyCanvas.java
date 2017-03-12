package converter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;

public class MyCanvas extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;

	KeyInput keyInput;
	Image imgBuf;
	Graphics gBuf;
	int state;
	Font dFont;
	Font font;
	char[] glyph;
	int[] glyphWidth;
	int glyphCnt;
	BufferedImage[] sheet;
	String outdir;
	int offsetX, offsetY;

	int glyphMax;
	String fontFile;
	float fontSize;
	int spriteSize;

	MyCanvas()
	{
		keyInput = new KeyInput();
		glyphCnt = 0;
		offsetX = 0;
		offsetY = 0;
		dFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		addKeyListener(keyInput);
		setFocusable(true);
	}

	public void init()
	{
		state = 0;
	}

	public void initThread()
	{
		Thread thread = new Thread(this);
		thread.start();
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	public void paint(Graphics g)
	{
		g.drawImage(imgBuf, 0, 0, this);
	}

	public void run()
	{
		imgBuf = createImage(Main.WID, Main.HEI);
		gBuf = imgBuf.getGraphics();

		for (;;)
		{
			gBuf.setColor(Color.white);
			gBuf.fillRect(0, 0, Main.WID, Main.HEI);

			switch (state)
			{
				case 0:
					loadSetting("setting.ini");
					glyph = new char[glyphMax];
					loadGlyph("glyph.txt");
					glyphWidth = new int[glyphCnt];
					sheet = new BufferedImage[glyphCnt / 256 + 1];
					font = loadFont(fontFile, fontSize);
					outdir = font.getFontName();
					state = 1;
					break;

				case 1:
					gBuf.setColor(Color.black);
					gBuf.setFont(dFont);
					gBuf.drawString("位置調整を行ってください", 80, 30);
					gBuf.drawString("移動 : 十字キー", 120, Main.HEI - 45);
					gBuf.drawString("決定 : Enter", 120, Main.HEI - 30);
					gBuf.drawRect((Main.WID - spriteSize) / 2, (Main.HEI - spriteSize) / 2,
							spriteSize, spriteSize);

					gBuf.setFont(font);
					int w = gBuf.getFontMetrics(font).charWidth('あ');
					gBuf.drawString("あ", (Main.WID - w - spriteSize) / 2 + offsetX,
							(Main.HEI - spriteSize) / 2 + offsetY);

					offsetX -= keyInput.keyLeft;
					offsetX += keyInput.keyRight;
					offsetY -= keyInput.keyUp;
					offsetY += keyInput.keyDown;

					if (keyInput.keyEnter == 1)
						state = 2;
					break;

				case 2:
					gBuf.setColor(Color.black);
					gBuf.setFont(dFont);
					gBuf.drawString("変換中...", 130, Main.HEI / 2);
					state = 3;
					break;

				case 3:
					deleteRecursive(new File(outdir));
					new File(outdir).mkdir();
					createImage();
					createGlyphData();
					createProperty();
					state = 4;
					break;

				case 4:
					gBuf.setColor(Color.black);
					gBuf.setFont(dFont);
					gBuf.drawString("変換完了", 125, Main.HEI / 2);
					break;

				default:
			}

			repaint();

			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	void loadSetting(String name)
	{
		try
		{
			FileReader fr = new FileReader(name);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("glyphMax="))
				{
					glyphMax = Integer.parseInt(line.substring(9));
				}
				else if (line.startsWith("fontFile="))
				{
					fontFile = line.substring(9);
				}
				else if (line.startsWith("fontSize="))
				{
					fontSize = Float.parseFloat(line.substring(9));
				}
				else if (line.startsWith("spriteSize="))
				{
					spriteSize = Integer.parseInt(line.substring(11));
				}
			}

			br.close();
			fr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	void loadGlyph(String name)
	{
		try
		{
			FileReader fr = new FileReader(name);
			int ch = fr.read();
			while (ch != -1)
			{
				// 改行は文字に含めない
				if (ch != 10 && ch != 13)
				{
					glyph[glyphCnt] = (char) ch;
					glyphCnt++;
				}
				ch = fr.read();
			}
			fr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	void createImage()
	{
		for (int i = 0; i < sheet.length; i++)
		{
			sheet[i] = new BufferedImage(spriteSize * 16, spriteSize * 16,
					BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = sheet[i].createGraphics();

			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2.setColor(new Color(0f, 0f, 0f, 0f));
			g2.fillRect(0, 0, spriteSize * 16, spriteSize * 16);

			g2.setFont(font);
			g2.setColor(new Color(1f, 1f, 1f, 1f));

			for (int j = 0; j < 256 && i * 256 + j < glyphCnt; j++)
			{
				glyphWidth[i * 256 + j] = g2.getFontMetrics(font).charWidth(glyph[i * 256 + j]);
				float x = j % 16 * spriteSize - (float) glyphWidth[i * 256 + j] / 2 + offsetX;
				float y = j / 16 * spriteSize + offsetY;
				g2.drawString(String.valueOf(glyph[i * 256 + j]), x, y);
			}

			try
			{
				ImageIO.write(sheet[i], "png", new File(outdir + "/sheet" + i + ".png"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	void createGlyphData()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(outdir + "/glyph.dat");
			DataOutputStream dos = new DataOutputStream(fos);
			for (int i = 0; i < glyphCnt; i++)
			{
				dos.writeChar(glyph[i]);
				dos.writeInt(glyphWidth[i]);
			}
			fos.close();
			dos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	void createProperty()
	{
		try
		{
			FileWriter fw = new FileWriter(outdir + "/font.prop", true);
			fw.write("fontName=" + font.getFontName());
			fw.write(System.getProperty("line.separator"));
			fw.write("fontSize=" + font.getSize());
			fw.write(System.getProperty("line.separator"));
			fw.write("glyphNum=" + glyphCnt);
			fw.write(System.getProperty("line.separator"));
			fw.write("sheetNum=" + sheet.length);
			fw.write(System.getProperty("line.separator"));
			fw.write("spriteSize=" + spriteSize);
			fw.write(System.getProperty("line.separator"));
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	Font loadFont(String filename, float size)
	{
		Font font = null;
		try
		{
			font = Font.createFont(Font.TRUETYPE_FONT, new File(filename));
			font = font.deriveFont(size);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return font;
	}

	boolean deleteRecursive(File file)
	{
		if (!file.exists())
		{
			return false;
		}

		if (file.isDirectory())
		{
			for (File child : file.listFiles())
			{
				deleteRecursive(child);
			}
		}
		return file.delete();
	}
}