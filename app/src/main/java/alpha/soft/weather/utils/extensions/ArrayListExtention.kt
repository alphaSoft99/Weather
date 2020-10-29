package alpha.soft.weather.utils.extensions

fun <E> java.util.ArrayList<E>.clearAndAddAll(values: Collection<E>): Boolean {
    clear()
    return addAll(values)
}

fun <E> java.util.ArrayList<E>.clearAndAdd(value: E): Boolean {
    clear()
    return add(value)
}