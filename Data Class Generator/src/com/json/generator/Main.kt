package com.json.generator

import com.json.generator.gui.Layout
import javax.swing.*

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel")
            } catch (ex: UnsupportedLookAndFeelException) {
                ex.printStackTrace()
            } catch (ex: IllegalAccessException) {
                ex.printStackTrace()
            } catch (ex: InstantiationException) {
                ex.printStackTrace()
            } catch (ex: ClassNotFoundException) {
                ex.printStackTrace()
            }

            SwingUtilities.invokeLater { Layout.createAndShowGUI() }
        }
    }
}
