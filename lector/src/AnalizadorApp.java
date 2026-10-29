import javax.swing.*;
import java.awt.FlowLayout;

public class AnalizadorApp extends JFrame {

    // Componentes de la interfaz
    JTextField txtEntrada = new JTextField(20);
    JButton btnAnalizarTexto = new JButton("Analizar Texto");
    JButton btnOperarNumero = new JButton("Operar Número");
    JButton btnJuego = new JButton("Juego");
    JLabel lblResultado = new JLabel("Resultado: ");
    JLabel lblError = new JLabel("");

    // Variable para el juego
    int numeroSecreto = (int) (Math.random() * 10) + 1;

    public AnalizadorApp() {
        // Configuración de la ventana
        setTitle("Analizador de Datos");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Agregar componentes a la ventana
        add(new JLabel("Ingrese un dato:"));
        add(txtEntrada);
        add(btnAnalizarTexto);
        add(btnOperarNumero);
        add(btnJuego);
        add(lblResultado);
        add(lblError);

        // 1. Lógica de Analizar Texto
        btnAnalizarTexto.addActionListener(e -> {
            lblError.setText("");
            String texto = txtEntrada.getText();
            int longitud = texto.length();
            String mayusculas = texto.toUpperCase();
            lblResultado.setText("Largo: " + longitud + " | " + mayusculas);
        });

        // 2. Lógica de Operar Número
        btnOperarNumero.addActionListener(e -> {
            lblError.setText("");
            try {
                double numero = Double.parseDouble(txtEntrada.getText());
                double raiz = Math.sqrt(numero);
                double cuadrado = Math.pow(numero, 2);
                lblResultado.setText("Raíz: " + raiz + " | Cuadrado: " + cuadrado);
            } catch (Exception ex) {
                lblError.setText("Error: Ingrese un número");
            }
        });

        // 3. Lógica del Juego
        btnJuego.addActionListener(e -> {
            lblError.setText("");
            try {
                int intento = Integer.parseInt(txtEntrada.getText());
                if (intento == numeroSecreto) {
                    lblResultado.setText("¡Acertaste el número!");
                    numeroSecreto = (int) (Math.random() * 10) + 1;
                } else {
                    lblResultado.setText("Incorrecto, intenta otra vez.");
                }
            } catch (Exception ex) {
                lblError.setText("Error: Ingrese un número entero");
            }
        });
    }

    public static void main(String[] args) {
        new AnalizadorApp().setVisible(true);
        }
}