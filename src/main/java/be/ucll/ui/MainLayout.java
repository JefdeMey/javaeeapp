package be.ucll.ui;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class MainLayout extends VerticalLayout implements RouterLayout {

	private final Div contentWrapper = new Div();

	public MainLayout() {
		setSizeFull();
		setPadding(false);
		setSpacing(false);

		contentWrapper.getStyle()
				.set("flex-grow", "1")
				.set("overflow", "auto");

		add(new HeaderComponent());
		addAndExpand(contentWrapper);
		add(new FooterComponent());
	}

	@Override
	public void showRouterLayoutContent(HasElement content) {
		contentWrapper.getElement().removeAllChildren();
		if (content != null) {
			contentWrapper.getElement().appendChild(content.getElement());
		}
	}
}
