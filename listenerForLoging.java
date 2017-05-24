package version2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class listenerForLoging implements ActionListener {

	Loging a;
	
	/*use button to connect window so that when you click one button another window will appear
	 *it is a Listener interface for receiving operating events.
	 *Classes that are interested in handling operating events can implement this interface, 
	 *and objects created using this class can be registered with the component using the addActionListener method of the component. 
	 *The actionPerformed method of the object is called when an event occurs.*/

	public listenerForLoging(Loging window) {
		a = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == a.Enter) {
			String user_name=a.name.getText();
			String pin=a.pin.getText();
			//In database query with username and pin corresponding account to add to the following
			new CourseTable();
			//when you click enter the coursetable will sppear
		}
		if (e.getSource() == a.Cancel) {
			a.dispose();
			//when you click cancel the window will close
		}
	}
}