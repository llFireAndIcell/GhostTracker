package me.fireandice.ghosttracker.config

import org.polyfrost.oneconfig.api.config.v1.*
import org.polyfrost.polyui.color.PolyColor
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.input.KeyBinder
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * An implementation of [org.polyfrost.oneconfig.api.config.v1.KtConfig] that doesn't allow for nullable types. Instead, if the property is null, it returns
 * the originally specified default value
 */
open class KtConfigNotNull(id: String, title: String, category: Category, icon: String? = null) :
    Config(id, icon, title, category) {

    @Suppress("UnstableApiUsage")
    final override fun makeTree(id: String) = Tree.tree(id)

    /**
     * return the property with the given id by a kotlin property reference.
     */
    @Suppress("UNCHECKED_CAST")
    protected val <V> KProperty<V>.property: Property<V> get() = (tree.getProp(this.name) as Property<V>)

    /**
     * create a new delegate for the given property.
     */
    @JvmSynthetic
    protected inline fun <reified T> property(
        def: T,
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null,
        visualizer: Class<out Visualizer>
    ) =
        Provider(def, name, description, category, subcategory, T::class.java, visualizer)

    @JvmSynthetic
    protected fun switch(
        def: Boolean = false,
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            Boolean::class.java,
            Visualizer.SwitchVisualizer::class.java
        )

    @JvmSynthetic
    protected fun color(
        def: PolyColor = rgba(0, 0, 0, 1f),
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            PolyColor::class.java,
            Visualizer.ColorVisualizer::class.java
        )

    @JvmSynthetic
    protected fun slider(
        min: Float = 0f,
        max: Float = 0f,
        def: Float = 0f,
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            Float::class.java,
            Visualizer.SliderVisualizer::class.java
        ) {
            addMetadata("min", min)
            addMetadata("max", max)
        }

    @JvmSynthetic
    protected fun text(
        def: String = "",
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            String::class.java,
            Visualizer.TextVisualizer::class.java
        )

    @JvmSynthetic
    protected fun keybind(
        def: KeyBinder.Bind,
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            KeyBinder.Bind::class.java,
            Visualizer.KeybindVisualizer::class.java
        )

    @JvmSynthetic
    protected fun radiobutton(
        options: Array<String>,
        def: Int = 0,
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            Int::class.java,
            Visualizer.RadioVisualizer::class.java
        ) {
            addMetadata("options", options)
        }

    @JvmSynthetic
    protected fun dropdown(
        options: Array<String>,
        def: Int = 0,
        name: String? = null,
        description: String? = null,
        category: String? = null,
        subcategory: String? = null
    ) =
        Provider(
            def,
            name,
            description,
            category,
            subcategory,
            Int::class.java,
            Visualizer.DropdownVisualizer::class.java
        ) {
            addMetadata("options", options)
        }

    protected class Provider<T>(
        private val def: T,
        private val name: String?,
        private val description: String?,
        private val category: String?,
        private val subcategory: String?,
        private val type: Class<T>,
        private val visualizer: Class<out Visualizer>,
        private val extra: (Property<T>.() -> Unit)? = null
    ) : PropertyDelegateProvider<KtConfigNotNull, ReadWriteProperty<KtConfigNotNull, T>> {

        override operator fun provideDelegate(
            thisRef: KtConfigNotNull,
            property: KProperty<*>
        ): ReadWriteProperty<KtConfigNotNull, T> {
            val p = Properties.simple(property.name, name ?: property.name, description, def, type)
            extra?.invoke(p)
            p.addMetadata("visualizer", visualizer)
            p.addMetadata("category", category)
            p.addMetadata("subcategory", subcategory)
            thisRef.tree.put(p)
            return PropertyDelegate(p, def) as ReadWriteProperty<KtConfigNotNull, T>
        }
    }

    protected class PropertyDelegate<T>(val property: Property<T>, val default: T) :
        ReadWriteProperty<KtConfigNotNull, T> {

        override operator fun getValue(thisRef: KtConfigNotNull, property: KProperty<*>): T =
            this.property.get() ?: default

        override operator fun setValue(thisRef: KtConfigNotNull, property: KProperty<*>, value: T) {
            this.property.set(value)
        }
    }
}
