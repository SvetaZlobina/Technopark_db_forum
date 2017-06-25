package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import api.daoFiles.DaoThread;
import api.models.ThreadModel;

import java.lang.reflect.InvocationTargetException;

public abstract class ServiceForController {

    @Autowired
    protected DaoThread daoThread;

    private static final String GET_THREAD_BY_ID = "getThreadById";
    private static final String GET_THREAD_BY_SLUG = "getThreadBySlug";
    private static final String GET_ID_BY_ID = "getIdById";
    private static final String GET_ID_BY_SLUG = "getIdBySlug";

    protected ThreadModel getThreadBySlug(final String slugOrId) {
        return getValueBySlug(
                slugOrId,
                GET_THREAD_BY_ID,
                GET_THREAD_BY_SLUG
        );
    }

    protected Integer getIdBySlug(final String slugOrId) {
        return getValueBySlug(
                slugOrId,
                GET_ID_BY_ID,
                GET_ID_BY_SLUG
        );
    }

    private <T, R> R getValueInside(final String getterName,
                                    final Class valueClass,
                                    final T value) {
        try {
            return (R) daoThread
                    .getClass()
                    .getMethod(getterName, valueClass)
                    .invoke(daoThread, value);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException err) {
            if (err.getCause().getClass().equals(EmptyResultDataAccessException.class)) {
                throw (EmptyResultDataAccessException) err.getCause();
            } else {
                return null;
            }
        }
    }

    private <T> T getValueBySlug(final String threadSlugOrId,
                                 final String idGetterName,
                                 final String slugGetterName) {
        try {
            try {

                Integer threadId = Integer.parseInt(threadSlugOrId);
                return getValueInside(idGetterName, Integer.class, threadId);
            } catch (DataAccessException errId) {

                return null;
            }
        } catch (NumberFormatException err) {
            try {

                return getValueInside(slugGetterName, String.class, threadSlugOrId);
            } catch (DataAccessException errSlug) {
                return null;
            }
        }
    }
}
