package metascripts

import org.gruth.demos.Euros


BigDecimal.metaClass.getEuros = {
    Euros.metaClass.invokeConstructor(delegate)
}

Integer.metaClass.getEuros = {
    Euros.metaClass.invokeConstructor(delegate)
}

String.metaClass.getEuros = {
    Euros.metaClass.invokeConstructor(delegate)
}
