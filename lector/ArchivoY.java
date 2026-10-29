import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ArchivoY extends JPanel implements KeyListener {
	private static final int ANCHO = 900;
	private static final int ALTO = 560;
	private static final int JUGADOR_ANCHO = 34;
	private static final int JUGADOR_ALTO = 58;
	private static final int SUELO = 470;

	private final Set<Integer> teclas = new HashSet<>();
	private final ArrayList<Plataforma> plataformas = new ArrayList<>();
	private final Timer reloj;
	private double x = 100;
	private double y = SUELO - JUGADOR_ALTO;
	private double velocidadY;
	private boolean enSuelo = true;
	private int direccion = 1;
	private int direccionEmbestida;
	private int tiempoEmbestida;
	private int ataque = -1;
	private int tiempoAtaque;
	private int proximoAtaque;
	private long ultimoIzquierda;
	private long ultimoDerecha;
	private String aviso = "";
	private int tiempoAviso;

	public ArchivoY() {
		setFocusable(true);
		addKeyListener(this);
		plataformas.add(new Plataforma(255, 390, 150, 18));
		plataformas.add(new Plataforma(490, 325, 170, 18));
		plataformas.add(new Plataforma(705, 400, 125, 18));
		reloj = new Timer(16, (ActionEvent e) -> actualizar());
		reloj.start();
	}

	private void actualizar() {
		if (tiempoAviso > 0) tiempoAviso--;
		if (tiempoEmbestida > 0) {
			x += direccionEmbestida * 11;
			tiempoEmbestida--;
		} else {
			if (teclas.contains(KeyEvent.VK_LEFT) || teclas.contains(KeyEvent.VK_A)) {
				x -= 4.2;
				direccion = -1;
			}
			if (teclas.contains(KeyEvent.VK_RIGHT) || teclas.contains(KeyEvent.VK_D)) {
				x += 4.2;
				direccion = 1;
			}
		}

		if (teclas.contains(KeyEvent.VK_SPACE) && enSuelo) {
			velocidadY = -12.5;
			enSuelo = false;
		}

		double anteriorY = y;
		velocidadY += 0.62;
		y += velocidadY;
		enSuelo = false;
		if (y + JUGADOR_ALTO >= SUELO) {
			y = SUELO - JUGADOR_ALTO;
			velocidadY = 0;
			enSuelo = true;
		}
		if (velocidadY >= 0) {
			for (Plataforma p : plataformas) {
				boolean cruzaPlataforma = anteriorY + JUGADOR_ALTO <= p.y
						&& y + JUGADOR_ALTO >= p.y;
				boolean solapaHorizontal = x + JUGADOR_ANCHO > p.x && x < p.x + p.ancho;
				if (cruzaPlataforma && solapaHorizontal) {
					y = p.y - JUGADOR_ALTO;
					velocidadY = 0;
					enSuelo = true;
				}
			}
		}

		x = Math.max(0, Math.min(ANCHO - JUGADOR_ANCHO, x));
		if (tiempoAtaque > 0 && --tiempoAtaque == 0) ataque = -1;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(new Color(17, 25, 48));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(new Color(33, 48, 76));
		for (int i = 0; i < 12; i++) {
			int bx = i * 95 - 20;
			g2.fillPolygon(new int[]{bx, bx + 65, bx + 130}, new int[]{SUELO, 250, SUELO}, 3);
		}
		dibujarBambues(g2);

		g2.setColor(new Color(68, 87, 102));
		g2.fillRect(0, SUELO, ANCHO, ALTO - SUELO);
		g2.setColor(new Color(116, 153, 135));
		g2.fillRect(0, SUELO, ANCHO, 6);
		for (Plataforma p : plataformas) {
			g2.setColor(new Color(71, 92, 102));
			g2.fillRoundRect(p.x, p.y, p.ancho, p.alto, 8, 8);
			g2.setColor(new Color(127, 170, 145));
			g2.fillRect(p.x, p.y, p.ancho, 4);
		}

		dibujarPersonaje(g2);
		dibujarInterfaz(g2);
		g2.dispose();
	}

	private void dibujarBambues(Graphics2D g) {
		int[][] tallos = {
			{35, 155}, {82, 270}, {145, 190}, {210, 115},
			{390, 235}, {450, 145}, {670, 210}, {760, 125},
			{825, 285}, {875, 175}
		};
		for (int[] tallo : tallos) {
			int bx = tallo[0];
			int base = SUELO;
			int alto = tallo[1];
			g.setColor(new Color(25, 68, 55, 190));
			g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(bx, base, bx + 2, base - alto);
			g.setColor(new Color(111, 151, 91, 210));
			g.setStroke(new BasicStroke(2));
			g.drawLine(bx - 2, base - 5, bx, base - alto + 4);
			for (int yNodo = base - 28; yNodo > base - alto; yNodo -= 42) {
				g.setColor(new Color(154, 177, 105, 220));
				g.fillRoundRect(bx - 6, yNodo, 13, 4, 3, 3);
				g.setColor(new Color(75, 111, 70, 210));
				g.drawLine(bx, yNodo, bx - 12, yNodo - 13);
				g.drawLine(bx + 1, yNodo - 2, bx + 13, yNodo - 15);
			}
		}
	}

	private void dibujarPersonaje(Graphics2D g) {
		int px = (int) x;
		int py = (int) y;
		g.setColor(new Color(0, 0, 0, 65));
		g.fillOval(px - 5, (int) (y + JUGADOR_ALTO - 3), 44, 9);
		g.setColor(new Color(30, 35, 48));
		g.fillRoundRect(px + 5, py + 21, 25, 35, 9, 9);
		g.setColor(new Color(194, 66, 66));
		g.fillRect(px + 6, py + 27, 24, 7);
		g.setColor(new Color(232, 193, 148));
		g.fillOval(px + 7, py + 1, 21, 23);
		g.setColor(new Color(36, 31, 39));
		g.fillArc(px + 5, py - 4, 25, 18, 0, 180);
		g.setColor(Color.WHITE);
		g.fillOval(px + (direccion > 0 ? 20 : 10), py + 10, 3, 3);
		g.setColor(new Color(194, 66, 66));
		g.fillRect(px + 9, py + 53, 7, 5);
		g.fillRect(px + 22, py + 53, 7, 5);

		Graphics2D espada = (Graphics2D) g.create();
		espada.translate(px + JUGADOR_ANCHO / 2, py + 32);
		espada.scale(direccion, 1);
		espada.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		espada.setColor(new Color(128, 69, 43));
		espada.drawLine(13, 0, 22, -2);
		espada.setStroke(new BasicStroke(2));
		espada.setColor(new Color(220, 184, 125));
		espada.drawLine(14, -5, 14, 5);
		espada.setColor(new Color(205, 221, 229));

		if (ataque >= 0) {
			int t = tiempoAtaque;
			if (ataque == 0) { // corte horizontal
				espada.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				espada.drawLine(22, -1, 85, -12 + (18 - t) / 2);
				espada.setColor(new Color(255, 246, 190, 160));
				espada.setStroke(new BasicStroke(2));
				espada.drawArc(24, -31, 67, 55, 205, 105);
			} else if (ataque == 1) { // tajo descendente
				espada.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				espada.drawLine(19, -7, 60, 34);
				espada.setColor(new Color(255, 246, 190, 150));
				espada.setStroke(new BasicStroke(2));
				espada.drawLine(24, -18, 66, 27);
			} else { // estocada
				espada.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				espada.drawLine(20, 0, 100, 0);
				espada.setColor(new Color(255, 246, 190, 170));
				espada.setStroke(new BasicStroke(2));
				espada.drawLine(25, -5, 96, -5);
			}
		} else {
			espada.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			espada.drawLine(20, -3, 63, -19);
			espada.setColor(new Color(235, 243, 244));
			espada.setStroke(new BasicStroke(2));
			espada.drawLine(25, -5, 66, -21);
		}
		espada.dispose();
	}

	private void dibujarInterfaz(Graphics2D g) {
		g.setColor(new Color(8, 12, 24, 205));
		g.fillRoundRect(18, 16, 478, 78, 14, 14);
		g.setFont(new Font("SansSerif", Font.BOLD, 18));
		g.setColor(new Color(242, 215, 164));
		g.drawString("RONIN: CAMINO DEL ODACHI", 32, 42);
		g.setFont(new Font("SansSerif", Font.PLAIN, 13));
		g.setColor(Color.WHITE);
		g.drawString("Mover: A/D o flechas   •   Saltar: ESPACIO", 32, 64);
		g.drawString("Atacar: J (3 técnicas)   •   Embestida: doble toque ←/→", 32, 83);
		if (tiempoAviso > 0) {
			g.setFont(new Font("SansSerif", Font.BOLD, 16));
			g.setColor(new Color(255, 232, 163));
			g.drawString(aviso, 625, 42);
		}
		if (ataque >= 0) {
			String[] nombres = {"Corte horizontal", "Tajo descendente", "Estocada"};
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.setColor(new Color(255, 231, 173));
			g.drawString(nombres[ataque], 735, 80);
		}
	}

	private void atacar() {
		if (tiempoAtaque == 0 && tiempoEmbestida == 0) {
			ataque = proximoAtaque;
			proximoAtaque = (proximoAtaque + 1) % 3;
			tiempoAtaque = 18;
			String[] nombres = {"Corte horizontal", "Tajo descendente", "Estocada"};
			aviso = nombres[ataque];
			tiempoAviso = 55;
		}
	}

	private void intentarEmbestida(int sentido) {
		long ahora = System.currentTimeMillis();
		long anterior = sentido < 0 ? ultimoIzquierda : ultimoDerecha;
		if (ahora - anterior < 280 && tiempoEmbestida == 0) {
			direccion = sentido;
			direccionEmbestida = sentido;
			tiempoEmbestida = 11;
			tiempoAtaque = 0;
			ataque = -1;
			aviso = "¡Embestida!";
			tiempoAviso = 45;
		}
		if (sentido < 0) ultimoIzquierda = ahora;
		else ultimoDerecha = ahora;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) intentarEmbestida(-1);
		if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) intentarEmbestida(1);
		if (k == KeyEvent.VK_J && !teclas.contains(k)) atacar();
		teclas.add(k);
	}

	@Override public void keyReleased(KeyEvent e) { teclas.remove(e.getKeyCode()); }
	@Override public void keyTyped(KeyEvent e) { }

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame ventana = new JFrame("Ronin: Camino del Odachi");
			ArchivoY juego = new ArchivoY();
			ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ventana.setContentPane(juego);
			ventana.setSize(ANCHO, ALTO);
			ventana.setResizable(false);
			ventana.setLocationRelativeTo(null);
			ventana.setVisible(true);
			juego.requestFocusInWindow();
		});
	}

	private static class Plataforma {
		final int x, y, ancho, alto;
		Plataforma(int x, int y, int ancho, int alto) {
			this.x = x;
			this.y = y;
			this.ancho = ancho;
			this.alto = alto;
		}
	}
}
