package org.daaya.daayalearningapp.exo.ui.about

import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    private val bodyHtml = " <html lang=\"en\"> " +
            "<p>We are a non-profit extending the reach of education to children around the world who have limited access educational tools and material.</p>"+
            "<br/>"+
            "<br/>"+
            "<p><a href=\"mailto:sangya@daaya.org\">Sangya padhi</a>, <em>Founder</em> </p>"+
            "<br/>"+
            "<p><a href=\"mailto:sangya@daaya.org\">Jagat Brahma</a>, <em>Co-Founder</em> </p>"+
            "<br/>"+
            "<p><a href=\"mailto:nirmit@daaya.org\">Nirmit Brahma</a>, <br/>" +
            " <em>Strategist and Technical Director</em> </p>"+
            "<br/>"+
            "<p><a href=\"mailto:gloria@daaya.org\">Gloria Matekenya</a>, <em>is Malawian</em> :"+
            "<br/>"+
            "I have a passion of solving problems that children and young adults face"+
            "in my country. I believe everyone can bring change with persistence and hard work with good"+
            "intentions.</p>"+
            "<br/>"+
            "</html>"


    private val _text = MutableLiveData<String>().apply {
        //value = "This is reflow Fragment"
        value = HtmlCompat.fromHtml(bodyHtml, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }
    val text: LiveData<String> = _text

}
