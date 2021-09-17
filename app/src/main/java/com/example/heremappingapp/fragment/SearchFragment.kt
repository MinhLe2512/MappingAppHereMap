package com.example.heremappingapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.FragmentSearchBinding


class SearchFragment : Fragment(R.layout.fragment_search) {
    private var binding: FragmentSearchBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(binding!!.searchToolbar)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search->{
                val searchView = item as SearchView
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
        return super.onOptionsItemSelected(item)
    }
}