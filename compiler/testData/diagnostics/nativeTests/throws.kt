// FILE: annotation.kt
package kotlin.native

annotation class Throws(vararg val exceptionClasses: kotlin.reflect.KClass<out Throwable>)

// FILE: test.kt
class Exception1 : Throwable()
class Exception2 : Throwable()
class Exception3 : Throwable()

<!THROWS_LIST_EMPTY!>@Throws<!>
fun foo() {}

interface Base0 {
    fun foo()
}

class ThrowsOnOverride : Base0 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception1::class)<!> override fun foo() {}
}

interface Base1 {
    @Throws(Exception1::class) fun foo()
}

class InheritsThrowsAndNoThrows : Base0, Base1 {
    <!INCOMPATIBLE_THROWS_INHERITED!>override fun foo() {}<!>
}

class OverridesThrowsAndNoThrows : Base0, Base1 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception1::class)<!> override fun foo() {}
}

class SameThrowsOnOverride : Base1 {
    @Throws(Exception1::class) override fun foo() {}
}

class DifferentThrowsOnOverride : Base1 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception2::class)<!> override fun foo() {}
}

class HasThrowsWithEmptyListOnOverride : Base1 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws<!> override fun foo() {}
}

interface Base2 {
    @Throws(Exception2::class) fun foo()
}

open class InheritsDifferentThrows1 : Base1, Base2 {
    <!INCOMPATIBLE_THROWS_INHERITED!>override fun foo() {}<!>
}

open class OverridesDifferentThrows1 : Base1, Base2 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception1::class)<!>override fun foo() {}
}

class InheritsDifferentThrowsThroughSameClass1 : InheritsDifferentThrows1() {
    override fun foo() {}
}

class OverridesDifferentThrowsThroughSameClass1 : InheritsDifferentThrows1() {
    @Throws(Exception1::class) override fun foo() {}
}

class OverridesDifferentThrowsThroughSameClass2 : InheritsDifferentThrows1() {
    @Throws(Exception2::class) override fun foo() {}
}

interface Base3 {
    @Throws(Exception3::class) fun foo()
}

class InheritsDifferentThrows2 : InheritsDifferentThrows1(), Base3 {
    <!INCOMPATIBLE_THROWS_INHERITED!>override fun foo() {}<!>
}

class OverridesDifferentThrows2 : InheritsDifferentThrows1(), Base3 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception3::class)<!>override fun foo() {}
}

open class OverridesDifferentThrows3 : Base1, Base2 {
    <!INCOMPATIBLE_THROWS_OVERRIDE, INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception3::class)<!>override fun foo() {}
}

class InheritsDifferentThrows3 : OverridesDifferentThrows3() {
    override fun foo() {}
}

class OverrideDifferentThrows4 : OverridesDifferentThrows3() {
    override fun foo() {}
}

class OverrideDifferentThrows5 : OverridesDifferentThrows3() {
    @Throws(Exception3::class) override fun foo() {}
}

class OverrideDifferentThrows6 : OverridesDifferentThrows3() {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception1::class)<!> override fun foo() {}
}

interface Base4 {
    @Throws(Exception1::class) fun foo()
}

class InheritsSameThrows : Base1, Base4 {
    override fun foo() {}
}

class OverridesSameThrows : Base1, Base4 {
    @Throws(Exception1::class) override fun foo() {}
}

class OverrideDifferentThrows7 : Base1, Base4 {
    <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception2::class)<!> override fun foo() {}
}

class OverrideDifferentThrows8 : Base1, Base3 {
    <!INCOMPATIBLE_THROWS_OVERRIDE, INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception2::class)<!> override fun foo() {}
}

interface Base5 {
    @Throws(Exception1::class, Exception2::class) fun foo()
}

interface Base6 {
    @Throws(Exception2::class, Exception1::class) fun foo()
}

class InheritsSameThrowsMultiple : Base5, Base6 {
    override fun foo() {}
}

class OverridesSameThrowsMultiple : Base5, Base6 {
    @Throws(Exception2::class, Exception1::class) override fun foo() {}
}

fun withLocalClass() {
    class LocalException : Throwable()

    abstract class Base7 {
        @Throws(Exception1::class, LocalException::class) abstract fun foo()
    }

    class InheritsDifferentThrowsLocal : Base1, Base7() {
        <!INCOMPATIBLE_THROWS_INHERITED!>override fun foo() {}<!>
    }

    class OverridesDifferentThrowsLocal : Base1, Base7() {
        <!INCOMPATIBLE_THROWS_OVERRIDE!>@Throws(Exception1::class, LocalException::class)<!>override fun foo() {}
    }
}
