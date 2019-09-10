package com.json.generator.gui

import com.json.generator.utils.GeneratorUtils
import com.json.generator.utils.DataClassGenerator
import com.json.generator.utils.DataClassType

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class Layout private constructor(name: String) : JFrame(name) {

    private val types = arrayOf("Moshi", "GSON")
    private val splitPane = JSplitPane()
    private val inputPanel = JPanel()
    private val outputPanel = JPanel()
    private val optionsPanel = JPanel()

    private val scrollPaneBottom = JScrollPane()
    private val scrollPanelInput = JScrollPane()
    private val inputField = JTextArea()
    private val outputField = JTextArea()
    private val btnGenerate = JButton("Generate")
    private val hintInput = JLabel("Paste here raw JSON String")
    private val hintOutput = JLabel("Generated Kotlin data classes")

    private val typesDropdown = JComboBox(types)

    fun addComponentsToPane(pane: Container) {

        pane.layout = GridLayout()
        pane.add(splitPane)
        pane.preferredSize = Dimension(800, 600)

        inputField.margin = Insets(10, 10, 10, 10)
        outputField.margin = Insets(10, 10, 10, 10)
        outputField.isEditable = false

        // let's configure our splitPane:
        splitPane.orientation = JSplitPane.VERTICAL_SPLIT
        splitPane.dividerLocation = 200
        splitPane.topComponent = inputPanel
        splitPane.bottomComponent = outputPanel

        inputPanel.layout = BorderLayout()
        outputPanel.layout = BorderLayout()
        optionsPanel.layout = GridLayout(0, 2)

        inputPanel.add(hintInput, BorderLayout.NORTH)
        inputPanel.add(scrollPanelInput, BorderLayout.CENTER)
        scrollPanelInput.setViewportView(inputField)

        optionsPanel.add(typesDropdown)
        optionsPanel.add(btnGenerate)

        inputPanel.add(optionsPanel, BorderLayout.SOUTH)

        outputPanel.add(hintOutput, BorderLayout.NORTH)
        outputPanel.add(scrollPaneBottom, BorderLayout.CENTER)
        scrollPaneBottom.setViewportView(outputField)

        btnGenerate.addActionListener(GenerateButtonClicked())
        pack()   // calling pack
    }

    private inner class GenerateButtonClicked : ActionListener {

        private var inputText = ""

        override fun actionPerformed(e: ActionEvent) {
            inputText = inputField.text
            val type = typesDropdown.selectedItem as String


            if (inputText.isEmpty()) {
                outputField.text = "Input text is empty!"
            } else if (!isJSONValid(inputText)) {
                outputField.text = "Invalid input! Not a valid Json Object/Array"
            } else {
                //json valid -- parse it
                DataClassGenerator().generateOutput(inputText, outputField, DataClassType.find(type))
            }
        }


        private fun isJSONValid(jsonInString: String): Boolean {
            return GeneratorUtils.getInstance().isJsonArray(jsonInString) || GeneratorUtils.getInstance().isJsonObject(
                jsonInString
            )
        }
    }

    companion object {

        @JvmStatic
        fun createAndShowGUI() {
            //Create and set up the window.
            val frame = Layout("JSON Data Class Generator")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            //Set up the content pane.
            frame.addComponentsToPane(frame.contentPane)
            //Display the window.
            frame.pack()
            frame.isVisible = true
        }
    }

}
