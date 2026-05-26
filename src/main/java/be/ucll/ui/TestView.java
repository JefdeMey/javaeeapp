package be.ucll.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import be.ucll.services.TestService;

@Route("test")
public class TestView extends VerticalLayout {

	@Autowired
	private TestService testService;

	public TestView() {

		Button button = new Button("Ophalen data");
		button.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
			@Override
			public void onComponentEvent(final ClickEvent<Button> buttonClickEvent) {
				testService.findAll().forEach(testEntity -> add(new Div("TestEntity value from database:" + testEntity.getValue())));
			}
		});

		add(button);
	}
}
