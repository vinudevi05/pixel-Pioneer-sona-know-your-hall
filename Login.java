package sonabookmyhall;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Login() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 900);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("LOGIN");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
        lblNewLabel.setBounds(335, 74, 161, 58);
        contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("FACULTY ID");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1.setBounds(130, 237, 115, 13);
        contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("FACULTY NAME");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_2.setBounds(130, 326, 115, 13);
        contentPane.add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("PASSWORD");
        lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_3.setBounds(130, 409, 115, 13);
        contentPane.add(lblNewLabel_3);

        textField = new JTextField();
        textField.setBounds(365, 234, 131, 19);
        contentPane.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setBounds(365, 323, 131, 19);
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(359, 406, 138, 19);
        contentPane.add(passwordField);

        JButton btnNewButton = new JButton("LOGIN");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String id = textField.getText();
                String name = textField_1.getText();
                String pass = new String(passwordField.getPassword());

                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Employee", "root", "Suba@3107");

                    PreparedStatement pst = con.prepareStatement("select * from faculty where faculty_name=? and faculty_id=? and password=?");
                    pst.setString(1, name);
                    pst.setString(2, id);
                    pst.setString(3, pass);

                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Login successful");
                        setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Login failed. Please check your credentials.");
                    }

                    pst.close();
                    con.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    // Handle any SQL errors
                }
            }
        });
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btnNewButton.setBounds(287, 537, 85, 21);
        contentPane.add(btnNewButton);
    }
}
