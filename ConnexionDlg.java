import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnexionDlg extends JDialog implements Parametres {
	
	public String pseudo;
	public String ipServeur;
	public String portServeur;
	public boolean ok;

	private final JPanel contentPanel = new JPanel();
	private JTextField jIpServeur;
	private JTextField jPseudo;
	private JTextField jPortServeur;

	public ConnexionDlg(Frame frame, boolean modal) {
		super(frame, modal);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.WEST);
			panel.setLayout(new GridLayout(3, 1, 0, 0));
			{
				JLabel lblNewLabel_2 = new JLabel("PSEUDO : ");
				panel.add(lblNewLabel_2);
			}
			{
				JLabel lblNewLabel = new JLabel("IP SERVEUR : ");
				panel.add(lblNewLabel);
			}
			{
				JLabel lblPort = new JLabel("PORT SERVEUR : ");
				panel.add(lblPort);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new GridLayout(3, 1, 0, 0));
			{
				jPseudo = new JTextField("");
				panel.add(jPseudo);
				jPseudo.setColumns(10);
			}
			{
				jIpServeur = new JTextField("");
				jIpServeur.setEnabled(false);
				panel.add(jIpServeur);
				jIpServeur.setColumns(10);
			}
			{
				jPortServeur = new JTextField("");
				jPortServeur.setEnabled(false);
				panel.add(jPortServeur);
				jPortServeur.setColumns(10);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						okButtonActionPerformed(arg0);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Annuler");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						cancelButtonactionPerformed(arg0);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

    	setSize(400,300);
    	setLocationRelativeTo(null);
    	setResizable(false);
    	ok = false;
	}
	
	public void okButtonActionPerformed(ActionEvent evt)
	{
		pseudo = jPseudo.getText();
		ipServeur = jIpServeur.getText();
		portServeur = jPortServeur.getText();;

		if(ipServeur.equals("") || pseudo.equals("") || portServeur.equals(""))
			JOptionPane.showMessageDialog(null, "Tout n'est pas rempli !", "", JOptionPane.WARNING_MESSAGE);
			
		else if(pseudo.length() > LONGUEUR_MAX_PSEUDO)
			JOptionPane.showMessageDialog(null, "Pseudo trop long (max 12 caractères)", "", JOptionPane.WARNING_MESSAGE);
		else
		{
			ok = true;
			setVisible(false);
		}
	}
	
	public void cancelButtonactionPerformed(ActionEvent evt)
	{
		setVisible(false);
	}

}
