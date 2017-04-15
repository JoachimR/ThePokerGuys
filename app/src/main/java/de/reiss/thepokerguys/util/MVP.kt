package de.reiss.thepokerguys.util

interface MVP {

    interface Model {

    }

    interface View {

        fun finish()

    }

    interface Presenter {

        val mvpView: View?

        val mvpModel: Model

    }

}
