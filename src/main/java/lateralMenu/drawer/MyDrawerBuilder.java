package lateralMenu.drawer;

import lateralMenu.form.TestForm;
import lateralMenu.main.Main;
import lateralMenu.tabbed.WindowsTabbed;
import menu.panels.pessoaPanel.PessoaEmpresaPanel;
import menu.panels.quartosPanel.AdicionarQuartoFrame;
import menu.panels.quartosPanel.RoomsPanel;
import menu.panels.reservasPanel.ReservasPanel;
import raven.drawer.component.SimpleDrawerBuilder;
import raven.drawer.component.footer.SimpleFooterData;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.menu.MenuAction;
import raven.drawer.component.menu.MenuEvent;
import raven.drawer.component.menu.MenuValidation;
import raven.drawer.component.menu.SimpleMenuOption;
import raven.swing.AvatarIcon;
import repository.QuartosRepository;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        return new SimpleHeaderData()
                .setIcon(new AvatarIcon(getClass().getResource("lateralMenu/image/sam.png"), 60, 60, 999))
                .setTitle("Sam Helson")
                .setDescription("sam@gmail.com");
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {
        String menus[][] = {
            {"~ESTATISTICAS~"},
            {"Dashboard"},
            {"~GERAL~"},
            {"Reservas"},
            {"Pernoites"},
            {"Apartamentos"},
            {"Day use"},
            {"~FINANCEIRO~"},
            {"Relatorios"},
            {"Precos"},
            {"~Social e estoque~"},
            {"Clientes"},
            {"Itens"},
            {"~Outros~"},
            {"Logout"}};

        String icons[] = {
            "dashboard.svg",
            "categoria2.svg",
            "chat.svg",
            "calendar.svg",
            "ui.svg",
            "forms.svg",
            "chart.svg",
            "icon.svg",
            "page.svg",
            "logout.svg"};

        return new SimpleMenuOption()
                .setMenus(menus)
//                .setIcons(icons)
                .setBaseIconPath("icon/menuIcons")
                .setIconScale(0.45f)
                .addMenuEvent(new MenuEvent() {
                    @Override
                    public void selected(MenuAction action, int index, int subIndex) {
                        if (index == 0) {
                            WindowsTabbed.getInstance().addTab("Dashboard", new TestForm());

                        } else if (index == 1) {
                            WindowsTabbed.getInstance().addTab("Reservas", new ReservasPanel());
                            new ReservasPanel();
                        }else if (index == 7) {
                            WindowsTabbed.getInstance().addTab("Clientes", new PessoaEmpresaPanel());
                        }else if (index == 3) {
                            WindowsTabbed.getInstance().addTab("Quartos", new RoomsPanel(new QuartosRepository()));
                        }else if (index == 2) {
                            WindowsTabbed.getInstance().addTab("Quartos", new RoomsPanel(new QuartosRepository()));
                        }else if (index == 9) {
                            Main.main.login();
                        }else if (index == 9) {
                            Main.main.login();
                        }else if (index == 9) {
                            Main.main.login();
                        }
                        System.out.println("Menu selected " + index + " " + subIndex);
                    }
                })
                .setMenuValidation(new MenuValidation() {
                    @Override
                    public boolean menuValidation(int index, int subIndex) {
                        return true;
                    }

                });
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Gestao e Automacao Hoteleira")
                .setDescription("SAM HELSON LTDA");
    }

    @Override
    public int getDrawerWidth() {
        return 275;
    }
}
