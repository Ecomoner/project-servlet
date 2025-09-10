package com.tictactoe;



import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Field field = extractField(session);
        int index = getSelectedIndex(req);
        Sign currentSing = field.getField().get(index);

        if(Sign.EMPTY != currentSing){
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/index");
            requestDispatcher.forward(req,resp);
            return;
        }

        field.getField().put(index,Sign.CROSS);
        if (checkWin(resp, session, field)) {
            return;
        }
        int emptyIndexField = field.getEmptyFieldIndex();
        if (emptyIndexField >= 0){
            field.getField().put(emptyIndexField,Sign.NOUGHT);
            if (checkWin(resp, session, field)) {
                return;
            }
        }else {
            session.setAttribute("draw",true);
            List<Sign> data = field.getFieldData();

            session.setAttribute("data",data);
            resp.sendRedirect("/index.jsp");
            return;
        }
        List<Sign> data = field.getFieldData();

        session.setAttribute("field",field);
        session.setAttribute("data",data);

        resp.sendRedirect("/index.jsp");

    }

    private Field extractField(HttpSession session) {
        Object fieldAttribute = session.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()){
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;

    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");

        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric? Integer.parseInt(click) : 0;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner){
            currentSession.setAttribute("winner",winner);
             List<Sign> data = field.getFieldData();

             currentSession.setAttribute("data",data);

             response.sendRedirect("/index.jsp");
             return true;
        }
        return false;


    }
}
