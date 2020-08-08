package squid.engine.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRegistry {
    private static Map<Class<? extends Event>, List<Method>> register = new HashMap<>();
    private static Map<Method, Class<?>> classregister = new HashMap<>();

    public static void register(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        if (methods.length < 1) return;
        for (Method method : methods) {
            Class<?>[] params = method.getParameterTypes();

            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }

            if (params.length != 1) {
                continue;
            }

            if (!Event.class.isAssignableFrom(params[0])) {
                continue;
            }

            List<Method> m = new ArrayList<>();
            register.computeIfAbsent((Class<? extends Event>) params[0], k -> m);
            m.add(method);
            classregister.putIfAbsent(method, clazz);
        }
    }

    public static void unRegister(Class<?> clazz) {
        List<Method> methods = List.of(clazz.getMethods());

        for (Class<? extends Event> event : register.keySet()) {
            for (Method method : register.get(event)) {
                if (methods.contains(method)) {
                    register.get(event).remove(method);
                }
            }
        }
    }

    public static void callEvent(final Event event) {
        new Thread(() -> {
            try {
                call(event);
            } catch(InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }, event.name).start();
    }

    private static void call(final Event event) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (register.get(event.getClass()) != null) {
            for (Method method : register.get(event.getClass())) {
                method.invoke(classregister.get(method).getConstructors()[0].newInstance(null), event);
            }
        }
    }
 }
