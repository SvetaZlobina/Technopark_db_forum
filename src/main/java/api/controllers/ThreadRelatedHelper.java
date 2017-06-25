package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import api.daoFiles.ThreadDAO;
import api.models.Thread;

import java.lang.reflect.InvocationTargetException;

public abstract class ThreadRelatedHelper {
    @Autowired
    protected ThreadDAO threadDAO;
    private static final String GET_THREAD_BY_ID = "getThreadById";
    private static final String GET_THREAD_BY_SLUG = "getThreadBySlug";
    private static final String GET_ID_BY_ID = "getIdById";
    private static final String GET_ID_BY_SLUG = "getIdBySlug";

    protected Thread getThreadBySlugOrId(String slugOrId) {
        return getValueBySlugOrId(
                slugOrId,
                GET_THREAD_BY_ID,
                GET_THREAD_BY_SLUG
        );
    }

    protected Integer getIdBySlugOrId(String slugOrId) {
        return getValueBySlugOrId(
                slugOrId,
                GET_ID_BY_ID,
                GET_ID_BY_SLUG
        );
    }

    private <T, R> R reflexiveGetValue(String getterName, Class valueClass, T value) {
        try {
            return (R) threadDAO
                    .getClass()
                    .getMethod(getterName, valueClass)
                    .invoke(threadDAO, value);
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

    private <T> T getValueBySlugOrId(String threadSlugOrId, String idGetterName, String slugGetterName) {
        try {
            try {
                final Integer threadId = Integer.parseInt(threadSlugOrId);
                return reflexiveGetValue(idGetterName, Integer.class, threadId);
            } catch (DataAccessException errId) {
                return null;
            }
        } catch (NumberFormatException err) {
            try {
                return reflexiveGetValue(slugGetterName, String.class, threadSlugOrId);
            } catch (DataAccessException errSlug) {
                return null;
            }
        }
    }
}
