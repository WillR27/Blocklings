package com.willr27.blocklings.interop;

import com.willr27.blocklings.Blocklings;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;

/**
 * A base class for all optional mod proxies.
 */
public class ModProxies
{
    /**
     * Initialises the mod proxies.
     */
    public static void init()
    {
        for (ModFileScanData modFileScanData : ModList.get().getAllScanData())
        {
            for (ModFileScanData.AnnotationData annotationData : modFileScanData.getAnnotations())
            {
                if (annotationData.getAnnotationType().equals(Type.getType(Proxy.class)))
                {
                    try
                    {
                        Class proxyClass = Class.forName(annotationData.getClassType().getClassName());
                        Proxy proxyAnnotation = (Proxy) proxyClass.getAnnotation(Proxy.class);

                        // Derived classes will be picked up by reflections but don't have an explicit annotationData, so they can be ignored here.
                        if (proxyAnnotation != null)
                        {
                            Field instanceField = proxyClass.getDeclaredField("instance");
                            String activeClassName = proxyAnnotation.activeClassName();

                            // If a class name is not specified then use the proxy class name and the "Active" prefix.
                            if (activeClassName.isEmpty())
                            {
                                activeClassName = proxyClass.getPackage().getName() + ".Active" + proxyClass.getSimpleName();
                            }

                            if (ModList.get().isLoaded(proxyAnnotation.modid()))
                            {
                                instanceField.set(null, Class.forName(activeClassName).asSubclass(proxyClass).newInstance());
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        Blocklings.LOGGER.error(String.format("Failed to create mod proxy for \"%s\" : %s", annotationData.getClassType().getClassName(), ex));
                    }
                }
            }
        }
    }
}
