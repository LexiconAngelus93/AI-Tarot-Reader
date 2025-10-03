# Reading History and Diary Implementation

## Overview

The Reading History and Diary feature allows users to maintain a comprehensive log of all their Tarot readings, add personal notes, and revisit past readings for reflection and insight tracking. This document outlines the implementation details for creating an organized, searchable, and insightful reading history system.

## User Flow

1. **History Overview**
   - User accesses the reading history section
   - User views a chronological list of past readings
   - User can filter and search through readings

2. **Reading Details**
   - User selects a specific reading to view in detail
   - User sees the complete spread, cards, and interpretation
   - User can add or edit personal notes

3. **Analysis and Patterns**
   - User can view statistics and patterns across readings
   - User can track recurring cards or themes

4. **Management**
   - User can organize readings into collections
   - User can export or share readings

## Implementation Details

### 1. Reading History Fragment

```kotlin
class ReadingHistoryFragment : Fragment() {
    private lateinit var viewModel: ReadingHistoryViewModel
    private lateinit var binding: FragmentReadingHistoryBinding
    private lateinit var readingAdapter: ReadingHistoryAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(ReadingHistoryViewModel::class.java)
        
        setupRecyclerView()
        setupSearchAndFilters()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        readingAdapter = ReadingHistoryAdapter { reading ->
            findNavController().navigate(
                ReadingHistoryFragmentDirections.actionToReadingDetail(reading.id)
            )
        }
        
        binding.readingsRecyclerView.apply {
            adapter = readingAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }
    
    private fun setupSearchAndFilters() {
        // Search functionality
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query ?: "")
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
        
        // Date filter
        binding.dateFilterButton.setOnClickListener {
            showDateFilterDialog()
        }
        
        // Spread filter
        binding.spreadFilterButton.setOnClickListener {
            showSpreadFilterDialog()
        }
        
        // Deck filter
        binding.deckFilterButton.setOnClickListener {
            showDeckFilterDialog()
        }
        
        // Sort order
        binding.sortButton.setOnClickListener {
            showSortOptionsDialog()
        }
    }
    
    private fun observeViewModel() {
        viewModel.readings.observe(viewLifecycleOwner) { readings ->
            readingAdapter.submitList(readings)
            binding.emptyStateGroup.isVisible = readings.isEmpty()
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        
        viewModel.activeFilters.observe(viewLifecycleOwner) { filters ->
            updateFilterChips(filters)
        }
    }
    
    private fun updateFilterChips(filters: ReadingFilters) {
        binding.filterChipGroup.removeAllViews()
        
        // Date range chip
        if (filters.startDate != null || filters.endDate != null) {
            val dateText = when {
                filters.startDate != null && filters.endDate != null -> {
                    "${formatDate(filters.startDate)} - ${formatDate(filters.endDate)}"
                }
                filters.startDate != null -> {
                    "After ${formatDate(filters.startDate)}"
                }
                else -> {
                    "Before ${formatDate(filters.endDate)}"
                }
            }
            
            addFilterChip("Date: $dateText") {
                viewModel.clearDateFilter()
            }
        }
        
        // Spread filter chip
        filters.spreadId?.let { spreadId ->
            val spreadName = viewModel.getSpreadName(spreadId)
            addFilterChip("Spread: $spreadName") {
                viewModel.clearSpreadFilter()
            }
        }
        
        // Deck filter chip
        filters.deckId?.let { deckId ->
            val deckName = viewModel.getDeckName(deckId)
            addFilterChip("Deck: $deckName") {
                viewModel.clearDeckFilter()
            }
        }
        
        // Search query chip
        if (filters.searchQuery.isNotEmpty()) {
            addFilterChip("Search: ${filters.searchQuery}") {
                viewModel.clearSearchQuery()
                binding.searchView.setQuery("", false)
            }
        }
        
        // Sort order chip
        val sortText = when (filters.sortOrder) {
            SortOrder.DATE_DESC -> "Newest first"
            SortOrder.DATE_ASC -> "Oldest first"
            SortOrder.ALPHABETICAL -> "A to Z"
        }
        addFilterChip("Sort: $sortText") {
            viewModel.setSortOrder(SortOrder.DATE_DESC) // Reset to default
        }
        
        binding.filterChipGroup.isVisible = binding.filterChipGroup.childCount > 0
    }
    
    private fun addFilterChip(text: String, onRemove: () -> Unit) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                onRemove()
            }
        }
        binding.filterChipGroup.addView(chip)
    }
    
    private fun showDateFilterDialog() {
        val dialog = DateRangeFilterDialogFragment()
        dialog.setOnDateRangeSelectedListener { startDate, endDate ->
            viewModel.setDateFilter(startDate, endDate)
        }
        dialog.show(childFragmentManager, "DateRangeFilterDialog")
    }
    
    private fun showSpreadFilterDialog() {
        val spreads = viewModel.getAllSpreads()
        val spreadNames = spreads.map { it.name }.toTypedArray()
        val spreadIds = spreads.map { it.id }.toLongArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.filter_by_spread)
            .setItems(spreadNames) { _, which ->
                viewModel.setSpreadFilter(spreadIds[which])
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun showDeckFilterDialog() {
        val decks = viewModel.getAllDecks()
        val deckNames = decks.map { it.name }.toTypedArray()
        val deckIds = decks.map { it.id }.toLongArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.filter_by_deck)
            .setItems(deckNames) { _, which ->
                viewModel.setDeckFilter(deckIds[which])
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun showSortOptionsDialog() {
        val sortOptions = arrayOf(
            "Newest first",
            "Oldest first",
            "Alphabetical (A to Z)"
        )
        
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.sort_by)
            .setItems(sortOptions) { _, which ->
                val sortOrder = when (which) {
                    0 -> SortOrder.DATE_DESC
                    1 -> SortOrder.DATE_ASC
                    2 -> SortOrder.ALPHABETICAL
                    else -> SortOrder.DATE_DESC
                }
                viewModel.setSortOrder(sortOrder)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
```

### 2. Reading History ViewModel

```kotlin
class ReadingHistoryViewModel : ViewModel() {
    private val readingRepository = ReadingRepository()
    private val spreadRepository = SpreadRepository()
    private val deckRepository = DeckRepository()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _readings = MutableLiveData<List<ReadingWithDetails>>()
    val readings: LiveData<List<ReadingWithDetails>> = _readings
    
    private val _activeFilters = MutableLiveData(ReadingFilters())
    val activeFilters: LiveData<ReadingFilters> = _activeFilters
    
    private var allSpreads: List<Spread> = emptyList()
    private var allDecks: List<Deck> = emptyList()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Load spreads and decks for filtering
            allSpreads = spreadRepository.getAllSpreads()
            allDecks = deckRepository.getAllDecks()
            
            // Load readings with initial filters
            loadReadings()
            
            _isLoading.value = false
        }
    }
    
    private fun loadReadings() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val filters = _activeFilters.value ?: ReadingFilters()
            val readingsWithDetails = readingRepository.getReadingsWithFilters(
                startDate = filters.startDate,
                endDate = filters.endDate,
                spreadId = filters.spreadId,
                deckId = filters.deckId,
                searchQuery = filters.searchQuery,
                sortOrder = filters.sortOrder
            )
            
            _readings.value = readingsWithDetails
            _isLoading.value = false
        }
    }
    
    fun setSearchQuery(query: String) {
        if (_activeFilters.value?.searchQuery != query) {
            _activeFilters.value = _activeFilters.value?.copy(searchQuery = query)
            loadReadings()
        }
    }
    
    fun clearSearchQuery() {
        if (_activeFilters.value?.searchQuery?.isNotEmpty() == true) {
            _activeFilters.value = _activeFilters.value?.copy(searchQuery = "")
            loadReadings()
        }
    }
    
    fun setDateFilter(startDate: Long?, endDate: Long?) {
        _activeFilters.value = _activeFilters.value?.copy(startDate = startDate, endDate = endDate)
        loadReadings()
    }
    
    fun clearDateFilter() {
        _activeFilters.value = _activeFilters.value?.copy(startDate = null, endDate = null)
        loadReadings()
    }
    
    fun setSpreadFilter(spreadId: Long) {
        _activeFilters.value = _activeFilters.value?.copy(spreadId = spreadId)
        loadReadings()
    }
    
    fun clearSpreadFilter() {
        _activeFilters.value = _activeFilters.value?.copy(spreadId = null)
        loadReadings()
    }
    
    fun setDeckFilter(deckId: Long) {
        _activeFilters.value = _activeFilters.value?.copy(deckId = deckId)
        loadReadings()
    }
    
    fun clearDeckFilter() {
        _activeFilters.value = _activeFilters.value?.copy(deckId = null)
        loadReadings()
    }
    
    fun setSortOrder(sortOrder: SortOrder) {
        _activeFilters.value = _activeFilters.value?.copy(sortOrder = sortOrder)
        loadReadings()
    }
    
    fun getAllSpreads(): List<Spread> = allSpreads
    
    fun getAllDecks(): List<Deck> = allDecks
    
    fun getSpreadName(spreadId: Long): String {
        return allSpreads.find { it.id == spreadId }?.name ?: "Unknown Spread"
    }
    
    fun getDeckName(deckId: Long): String {
        return allDecks.find { it.id == deckId }?.name ?: "Unknown Deck"
    }
}

data class ReadingFilters(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val spreadId: Long? = null,
    val deckId: Long? = null,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.DATE_DESC
)

enum class SortOrder {
    DATE_DESC,
    DATE_ASC,
    ALPHABETICAL
}
```

### 3. Reading History Adapter

```kotlin
class ReadingHistoryAdapter(
    private val onReadingClick: (ReadingWithDetails) -> Unit
) : ListAdapter<ReadingWithDetails, ReadingHistoryViewHolder>(ReadingDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingHistoryViewHolder {
        val binding = ItemReadingHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReadingHistoryViewHolder(binding, onReadingClick)
    }
    
    override fun onBindViewHolder(holder: ReadingHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ReadingHistoryViewHolder(
    private val binding: ItemReadingHistoryBinding,
    private val onReadingClick: (ReadingWithDetails) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(reading: ReadingWithDetails) {
        binding.readingTitle.text = reading.reading.title
        binding.readingDate.text = formatDate(reading.reading.createdAt)
        binding.spreadName.text = reading.spread.name
        binding.deckName.text = reading.deck.name
        
        // Set up card preview
        setupCardPreview(reading.cards)
        
        // Set up interpretation preview
        binding.interpretationPreview.text = reading.reading.interpretation
            .take(100)
            .plus(if (reading.reading.interpretation.length > 100) "..." else "")
        
        // Set up notes indicator
        binding.notesIndicator.isVisible = reading.notes.isNotEmpty()
        
        // Set up click listener
        binding.root.setOnClickListener {
            onReadingClick(reading)
        }
    }
    
    private fun setupCardPreview(cards: List<ReadingCard>) {
        binding.cardPreviewLayout.removeAllViews()
        
        // Show up to 3 cards as preview
        val cardsToShow = cards.take(3)
        
        for (card in cardsToShow) {
            val cardView = ImageView(binding.root.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(40),
                    dpToPx(60)
                ).apply {
                    marginEnd = dpToPx(8)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            
            // Load card image
            Glide.with(binding.root)
                .load(card.card.imageUrl)
                .transform(if (card.isReversed) RotateTransformation(180f) else NoTransformation())
                .into(cardView)
            
            binding.cardPreviewLayout.addView(cardView)
        }
        
        // Add indicator if there are more cards
        if (cards.size > 3) {
            val moreIndicator = TextView(binding.root.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "+${cards.size - 3}"
                setTextColor(Color.WHITE)
                textSize = 12f
                gravity = Gravity.CENTER
            }
            binding.cardPreviewLayout.addView(moreIndicator)
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * binding.root.resources.displayMetrics.density).toInt()
    }
}

class ReadingDiffCallback : DiffUtil.ItemCallback<ReadingWithDetails>() {
    override fun areItemsTheSame(oldItem: ReadingWithDetails, newItem: ReadingWithDetails): Boolean {
        return oldItem.reading.id == newItem.reading.id
    }
    
    override fun areContentsTheSame(oldItem: ReadingWithDetails, newItem: ReadingWithDetails): Boolean {
        return oldItem == newItem
    }
}

class RotateTransformation(private val rotationDegrees: Float) : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees)
        return Bitmap.createBitmap(
            toTransform, 0, 0, toTransform.width, toTransform.height, matrix, true
        )
    }
    
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(("rotate$rotationDegrees").toByteArray())
    }
}

class NoTransformation : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return toTransform
    }
    
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("no_transformation".toByteArray())
    }
}
```

### 4. Date Range Filter Dialog

```kotlin
class DateRangeFilterDialogFragment : DialogFragment() {
    private lateinit var binding: DialogDateRangeFilterBinding
    
    private var startDate: Long? = null
    private var endDate: Long? = null
    
    private var onDateRangeSelectedListener: ((Long?, Long?) -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDateRangeFilterBinding.inflate(layoutInflater)
        
        // Set up the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.filter_by_date)
            .setView(binding.root)
            .setPositiveButton(R.string.apply, null) // Set listener later to prevent auto-dismiss
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.clear) { _, _ ->
                onDateRangeSelectedListener?.invoke(null, null)
            }
            .create()
        
        setupDatePickers()
        
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        
        // Override positive button to validate before dismissing
        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (validateDates()) {
                onDateRangeSelectedListener?.invoke(startDate, endDate)
                dismiss()
            }
        }
    }
    
    private fun setupDatePickers() {
        // Start date picker
        binding.startDateButton.setOnClickListener {
            showDatePicker(isStartDate = true)
        }
        
        // End date picker
        binding.endDateButton.setOnClickListener {
            showDatePicker(isStartDate = false)
        }
    }
    
    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        
        // Set initial date if already selected
        if (isStartDate && startDate != null) {
            calendar.timeInMillis = startDate!!
        } else if (!isStartDate && endDate != null) {
            calendar.timeInMillis = endDate!!
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                
                if (isStartDate) {
                    startDate = calendar.timeInMillis
                    binding.startDateText.text = formatDate(startDate!!)
                    binding.startDateText.visibility = View.VISIBLE
                } else {
                    endDate = calendar.timeInMillis
                    binding.endDateText.text = formatDate(endDate!!)
                    binding.endDateText.visibility = View.VISIBLE
                }
            },
            year,
            month,
            day
        )
        
        // Set date constraints
        if (isStartDate && endDate != null) {
            datePickerDialog.datePicker.maxDate = endDate!!
        } else if (!isStartDate && startDate != null) {
            datePickerDialog.datePicker.minDate = startDate!!
        }
        
        datePickerDialog.show()
    }
    
    private fun validateDates(): Boolean {
        // If both dates are set, ensure start date is before end date
        if (startDate != null && endDate != null && startDate!! > endDate!!) {
            Toast.makeText(
                requireContext(),
                R.string.start_date_must_be_before_end_date,
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        return true
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
    
    fun setOnDateRangeSelectedListener(listener: (Long?, Long?) -> Unit) {
        onDateRangeSelectedListener = listener
    }
}
```

### 5. Reading Detail Fragment

```kotlin
class ReadingDetailFragment : Fragment() {
    private lateinit var viewModel: ReadingDetailViewModel
    private lateinit var binding: FragmentReadingDetailBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val readingId = args.readingId
        
        viewModel = ViewModelProvider(
            this,
            ReadingDetailViewModelFactory(readingId)
        ).get(ReadingDetailViewModel::class.java)
        
        setupSpreadView()
        setupNotesSection()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupSpreadView() {
        binding.spreadView.setOnCardClickListener { card, position ->
            showCardDetailDialog(card, position)
        }
    }
    
    private fun setupNotesSection() {
        binding.addNoteButton.setOnClickListener {
            showAddNoteDialog()
        }
    }
    
    private fun setupButtons() {
        binding.shareButton.setOnClickListener {
            shareReading()
        }
        
        binding.exportButton.setOnClickListener {
            exportReadingAsPdf()
        }
        
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }
    
    private fun observeViewModel() {
        viewModel.readingWithDetails.observe(viewLifecycleOwner) { readingDetails ->
            updateUI(readingDetails)
        }
        
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            updateNotesSection(notes)
        }
    }
    
    private fun updateUI(readingDetails: ReadingWithDetails) {
        // Set basic reading info
        binding.readingTitle.text = readingDetails.reading.title
        binding.readingDate.text = formatDate(readingDetails.reading.createdAt)
        binding.spreadName.text = readingDetails.spread.name
        binding.deckName.text = readingDetails.deck.name
        
        // Set interpretation
        binding.interpretationText.text = readingDetails.reading.interpretation
        
        // Set eigenvalue info
        binding.eigenvalueText.text = getString(
            R.string.eigenvalue_format,
            readingDetails.reading.eigenvalue,
            readingDetails.eigenvalueCard.name
        )
        
        // Load eigenvalue card image
        Glide.with(requireContext())
            .load(readingDetails.eigenvalueCard.imageUrl)
            .into(binding.eigenvalueCardImage)
        
        // Set up spread view
        binding.spreadView.setReadingCards(readingDetails.cards)
    }
    
    private fun updateNotesSection(notes: List<Note>) {
        binding.notesContainer.removeAllViews()
        
        if (notes.isEmpty()) {
            binding.noNotesText.visibility = View.VISIBLE
        } else {
            binding.noNotesText.visibility = View.GONE
            
            // Add notes in reverse chronological order (newest first)
            for (note in notes.sortedByDescending { it.createdAt }) {
                val noteView = layoutInflater.inflate(
                    R.layout.item_reading_note,
                    binding.notesContainer,
                    false
                )
                
                val noteText = noteView.findViewById<TextView>(R.id.noteText)
                val noteDate = noteView.findViewById<TextView>(R.id.noteDate)
                val editButton = noteView.findViewById<ImageButton>(R.id.editNoteButton)
                val deleteButton = noteView.findViewById<ImageButton>(R.id.deleteNoteButton)
                
                noteText.text = note.content
                noteDate.text = formatDate(note.createdAt)
                
                editButton.setOnClickListener {
                    showEditNoteDialog(note)
                }
                
                deleteButton.setOnClickListener {
                    showDeleteNoteConfirmationDialog(note)
                }
                
                binding.notesContainer.addView(noteView)
            }
        }
    }
    
    private fun showCardDetailDialog(card: Card, position: SpreadPosition) {
        val dialog = CardDetailDialogFragment.newInstance(
            cardId = card.id,
            positionName = position.name,
            isReversed = viewModel.isCardReversed(card.id)
        )
        dialog.show(childFragmentManager, "CardDetailDialog")
    }
    
    private fun showAddNoteDialog() {
        val dialog = AddNoteDialogFragment()
        dialog.setOnNoteAddedListener { noteText ->
            viewModel.addNote(noteText)
        }
        dialog.show(childFragmentManager, "AddNoteDialog")
    }
    
    private fun showEditNoteDialog(note: Note) {
        val dialog = EditNoteDialogFragment.newInstance(note.id, note.content)
        dialog.setOnNoteEditedListener { noteId, noteText ->
            viewModel.updateNote(noteId, noteText)
        }
        dialog.show(childFragmentManager, "EditNoteDialog")
    }
    
    private fun showDeleteNoteConfirmationDialog(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_note)
            .setMessage(R.string.delete_note_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteNote(note.id)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun shareReading() {
        viewModel.generateReadingImage { bitmap ->
            if (bitmap != null) {
                // Save bitmap to cache directory
                val file = File(requireContext().cacheDir, "reading_${viewModel.readingId}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                
                // Create sharing intent
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, viewModel.readingWithDetails.value?.reading?.title)
                    putExtra(Intent.EXTRA_TEXT, viewModel.getShareText())
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(intent, getString(R.string.share_reading)))
            }
        }
    }
    
    private fun exportReadingAsPdf() {
        viewModel.exportReadingAsPdf(requireContext()) { file ->
            if (file != null) {
                // Create intent to view PDF
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(intent, getString(R.string.view_pdf)))
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.pdf_export_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_reading)
            .setMessage(R.string.delete_reading_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteReading()
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}
```

### 6. Reading Detail ViewModel

```kotlin
class ReadingDetailViewModel(
    val readingId: Long
) : ViewModel() {
    private val readingRepository = ReadingRepository()
    private val noteRepository = NoteRepository()
    
    private val _readingWithDetails = MutableLiveData<ReadingWithDetails>()
    val readingWithDetails: LiveData<ReadingWithDetails> = _readingWithDetails
    
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes
    
    init {
        loadReading()
        loadNotes()
    }
    
    private fun loadReading() {
        viewModelScope.launch {
            val reading = readingRepository.getReadingWithDetailsById(readingId)
            _readingWithDetails.value = reading
        }
    }
    
    private fun loadNotes() {
        viewModelScope.launch {
            val notes = noteRepository.getNotesForReading(readingId)
            _notes.value = notes
        }
    }
    
    fun isCardReversed(cardId: Long): Boolean {
        return _readingWithDetails.value?.cards?.find { it.card.id == cardId }?.isReversed ?: false
    }
    
    fun addNote(content: String) {
        viewModelScope.launch {
            val note = Note(
                id = 0, // Room will generate ID
                readingId = readingId,
                content = content,
                createdAt = System.currentTimeMillis()
            )
            
            noteRepository.addNote(note)
            loadNotes()
        }
    }
    
    fun updateNote(noteId: Long, content: String) {
        viewModelScope.launch {
            noteRepository.updateNoteContent(noteId, content)
            loadNotes()
        }
    }
    
    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
            loadNotes()
        }
    }
    
    fun deleteReading() {
        viewModelScope.launch {
            readingRepository.deleteReading(readingId)
        }
    }
    
    fun generateReadingImage(callback: (Bitmap?) -> Unit) {
        val reading = _readingWithDetails.value ?: return callback(null)
        
        // Create bitmap with appropriate size
        val bitmap = Bitmap.createBitmap(1200, 1800, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Fill background
        canvas.drawColor(Color.rgb(25, 25, 40))
        
        // Draw title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(reading.reading.title, 600f, 100f, titlePaint)
        
        // Draw spread name
        val spreadNamePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(reading.spread.name, 600f, 160f, spreadNamePaint)
        
        // Draw date
        val datePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            formatDate(reading.reading.createdAt),
            600f,
            210f,
            datePaint
        )
        
        // Draw cards
        val cardWidth = 200f
        val cardHeight = 350f
        
        // Calculate positions based on spread layout
        val cardPositions = mutableListOf<RectF>()
        for (position in reading.spread.positions) {
            val x = 600f + (position.xPosition - 0.5f) * 800f
            val y = 600f + (position.yPosition - 0.5f) * 600f
            
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            cardPositions.add(rect)
        }
        
        // Draw cards on canvas
        for (i in reading.cards.indices) {
            val readingCard = reading.cards[i]
            val rect = cardPositions.getOrNull(i) ?: continue
            
            // Load card image
            val cardBitmap = loadCardBitmap(readingCard.card.imageUrl)
            if (cardBitmap != null) {
                // Apply rotation if card is reversed
                if (readingCard.isReversed) {
                    val matrix = Matrix()
                    matrix.setRotate(180f, cardBitmap.width / 2f, cardBitmap.height / 2f)
                    
                    val rotatedBitmap = Bitmap.createBitmap(
                        cardBitmap,
                        0,
                        0,
                        cardBitmap.width,
                        cardBitmap.height,
                        matrix,
                        true
                    )
                    
                    canvas.drawBitmap(rotatedBitmap, null, rect, null)
                } else {
                    canvas.drawBitmap(cardBitmap, null, rect, null)
                }
            }
            
            // Draw position name
            val positionPaint = Paint().apply {
                color = Color.WHITE
                textSize = 24f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                readingCard.position.name,
                rect.centerX(),
                rect.bottom + 30f,
                positionPaint
            )
        }
        
        // Draw eigenvalue
        val eigenvaluePaint = Paint().apply {
            color = Color.YELLOW
            textSize = 36f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}",
            600f,
            1100f,
            eigenvaluePaint
        )
        
        // Draw interpretation (truncated)
        val interpretationPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.LEFT
        }
        
        val interpretation = reading.reading.interpretation
        val truncatedInterpretation = if (interpretation.length > 300) {
            interpretation.substring(0, 300) + "..."
        } else {
            interpretation
        }
        
        val textLayout = StaticLayout.Builder.obtain(
            truncatedInterpretation,
            0,
            truncatedInterpretation.length,
            TextPaint(interpretationPaint),
            1000
        ).build()
        
        canvas.save()
        canvas.translate(100f, 1200f)
        textLayout.draw(canvas)
        canvas.restore()
        
        // Draw app name and date at bottom
        val footerPaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Created with Tarot Reading App • ${formatDate(System.currentTimeMillis())}",
            600f,
            1750f,
            footerPaint
        )
        
        callback(bitmap)
    }
    
    private fun loadCardBitmap(imageUrl: String): Bitmap? {
        try {
            return Glide.with(application)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    fun getShareText(): String {
        val reading = _readingWithDetails.value ?: return ""
        
        return """
            ${reading.reading.title}
            
            Spread: ${reading.spread.name}
            Date: ${formatDate(reading.reading.createdAt)}
            
            ${reading.reading.interpretation.take(300)}...
            
            Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}
            
            Created with Tarot Reading App
        """.trimIndent()
    }
    
    fun exportReadingAsPdf(context: Context, callback: (File?) -> Unit) {
        val reading = _readingWithDetails.value ?: return callback(null)
        
        viewModelScope.launch {
            try {
                // Create PDF document
                val document = PdfDocument()
                
                // Create page
                val pageInfo = PdfDocument.PageInfo.Builder(1200, 1800, 1).create()
                val page = document.startPage(pageInfo)
                
                // Draw content on page canvas
                generateReadingImage { bitmap ->
                    if (bitmap != null) {
                        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                        document.finishPage(page)
                        
                        // Add notes page if there are notes
                        val notes = _notes.value
                        if (!notes.isNullOrEmpty()) {
                            val notesPageInfo = PdfDocument.PageInfo.Builder(1200, 1800, 2).create()
                            val notesPage = document.startPage(notesPageInfo)
                            
                            drawNotesPage(notesPage.canvas, notes)
                            
                            document.finishPage(notesPage)
                        }
                        
                        // Save PDF to file
                        val file = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                            "tarot_reading_${readingId}_${System.currentTimeMillis()}.pdf"
                        )
                        
                        FileOutputStream(file).use { out ->
                            document.writeTo(out)
                        }
                        
                        document.close()
                        callback(file)
                    } else {
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
    
    private fun drawNotesPage(canvas: Canvas, notes: List<Note>) {
        // Fill background
        canvas.drawColor(Color.rgb(25, 25, 40))
        
        // Draw title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Reading Notes", 600f, 100f, titlePaint)
        
        // Draw notes
        val notePaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.LEFT
        }
        
        val dateTimePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 24f
            textAlign = Paint.Align.LEFT
        }
        
        var yPosition = 200f
        
        for (note in notes.sortedBy { it.createdAt }) {
            // Draw date
            canvas.drawText(
                formatDate(note.createdAt),
                100f,
                yPosition,
                dateTimePaint
            )
            
            yPosition += 40f
            
            // Draw note content
            val textLayout = StaticLayout.Builder.obtain(
                note.content,
                0,
                note.content.length,
                TextPaint(notePaint),
                1000
            ).build()
            
            canvas.save()
            canvas.translate(100f, yPosition)
            textLayout.draw(canvas)
            canvas.restore()
            
            yPosition += textLayout.height + 60f
            
            // Draw separator line
            val linePaint = Paint().apply {
                color = Color.GRAY
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            
            canvas.drawLine(100f, yPosition - 30f, 1100f, yPosition - 30f, linePaint)
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}

class ReadingDetailViewModelFactory(private val readingId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReadingDetailViewModel(readingId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

### 7. Note Dialog Fragments

```kotlin
class AddNoteDialogFragment : DialogFragment() {
    private lateinit var binding: DialogAddNoteBinding
    
    private var onNoteAddedListener: ((String) -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddNoteBinding.inflate(layoutInflater)
        
        // Set up the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_note)
            .setView(binding.root)
            .setPositiveButton(R.string.save, null) // Set listener later to prevent auto-dismiss
            .setNegativeButton(R.string.cancel, null)
            .create()
        
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        
        // Override positive button to validate before dismissing
        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val noteText = binding.noteInput.text.toString().trim()
            
            if (noteText.isNotEmpty()) {
                onNoteAddedListener?.invoke(noteText)
                dismiss()
            } else {
                binding.noteInput.error = getString(R.string.note_cannot_be_empty)
            }
        }
    }
    
    fun setOnNoteAddedListener(listener: (String) -> Unit) {
        onNoteAddedListener = listener
    }
}

class EditNoteDialogFragment : DialogFragment() {
    private lateinit var binding: DialogEditNoteBinding
    
    private var noteId: Long = 0
    private var noteContent: String = ""
    
    private var onNoteEditedListener: ((Long, String) -> Unit)? = null
    
    companion object {
        private const val ARG_NOTE_ID = "note_id"
        private const val ARG_NOTE_CONTENT = "note_content"
        
        fun newInstance(noteId: Long, noteContent: String): EditNoteDialogFragment {
            val fragment = EditNoteDialogFragment()
            val args = Bundle()
            args.putLong(ARG_NOTE_ID, noteId)
            args.putString(ARG_NOTE_CONTENT, noteContent)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        arguments?.let {
            noteId = it.getLong(ARG_NOTE_ID)
            noteContent = it.getString(ARG_NOTE_CONTENT) ?: ""
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditNoteBinding.inflate(layoutInflater)
        
        // Set initial note content
        binding.noteInput.setText(noteContent)
        
        // Set up the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_note)
            .setView(binding.root)
            .setPositiveButton(R.string.save, null) // Set listener later to prevent auto-dismiss
            .setNegativeButton(R.string.cancel, null)
            .create()
        
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        
        // Override positive button to validate before dismissing
        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val noteText = binding.noteInput.text.toString().trim()
            
            if (noteText.isNotEmpty()) {
                onNoteEditedListener?.invoke(noteId, noteText)
                dismiss()
            } else {
                binding.noteInput.error = getString(R.string.note_cannot_be_empty)
            }
        }
    }
    
    fun setOnNoteEditedListener(listener: (Long, String) -> Unit) {
        onNoteEditedListener = listener
    }
}
```

## Reading Analytics and Insights

### 1. Reading Analytics Fragment

```kotlin
class ReadingAnalyticsFragment : Fragment() {
    private lateinit var viewModel: ReadingAnalyticsViewModel
    private lateinit var binding: FragmentReadingAnalyticsBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(ReadingAnalyticsViewModel::class.java)
        
        setupCharts()
        observeViewModel()
    }
    
    private fun setupCharts() {
        // Set up card frequency chart
        binding.cardFrequencyChart.apply {
            description.isEnabled = false
            legend.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
        }
        
        // Set up suit distribution chart
        binding.suitDistributionChart.apply {
            description.isEnabled = false
            legend.textColor = Color.WHITE
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Suit Distribution"
            setCenterTextColor(Color.WHITE)
            setCenterTextSize(16f)
        }
        
        // Set up reading frequency chart
        binding.readingFrequencyChart.apply {
            description.isEnabled = false
            legend.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
        }
    }
    
    private fun observeViewModel() {
        viewModel.cardFrequencyData.observe(viewLifecycleOwner) { data ->
            updateCardFrequencyChart(data)
        }
        
        viewModel.suitDistributionData.observe(viewLifecycleOwner) { data ->
            updateSuitDistributionChart(data)
        }
        
        viewModel.readingFrequencyData.observe(viewLifecycleOwner) { data ->
            updateReadingFrequencyChart(data)
        }
        
        viewModel.mostCommonCards.observe(viewLifecycleOwner) { cards ->
            updateMostCommonCardsSection(cards)
        }
        
        viewModel.readingStats.observe(viewLifecycleOwner) { stats ->
            updateReadingStatsSection(stats)
        }
    }
    
    private fun updateCardFrequencyChart(data: Map<String, Int>) {
        // Sort data by frequency (descending)
        val sortedData = data.entries.sortedByDescending { it.value }.take(10)
        
        val entries = sortedData.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Card Frequency").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }
        
        val barData = BarData(dataSet)
        binding.cardFrequencyChart.data = barData
        
        // Set X axis labels
        binding.cardFrequencyChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(sortedData.map { it.key })
            labelRotationAngle = 45f
            labelCount = sortedData.size
        }
        
        binding.cardFrequencyChart.invalidate()
    }
    
    private fun updateSuitDistributionChart(data: Map<String, Int>) {
        val entries = data.map { entry ->
            PieEntry(entry.value.toFloat(), entry.key)
        }
        
        val dataSet = PieDataSet(entries, "Suits").apply {
            colors = listOf(
                Color.rgb(255, 152, 0),  // Wands (Orange)
                Color.rgb(33, 150, 243), // Cups (Blue)
                Color.rgb(244, 67, 54),  // Swords (Red)
                Color.rgb(76, 175, 80),  // Pentacles (Green)
                Color.rgb(156, 39, 176)  // Major Arcana (Purple)
            )
            valueTextColor = Color.WHITE
            valueTextSize = 14f
            valueFormatter = PercentFormatter(binding.suitDistributionChart)
        }
        
        val pieData = PieData(dataSet)
        binding.suitDistributionChart.data = pieData
        binding.suitDistributionChart.invalidate()
    }
    
    private fun updateReadingFrequencyChart(data: Map<String, Int>) {
        val entries = data.entries.sortedBy { it.key }.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Reading Frequency").apply {
            color = Color.rgb(64, 89, 128)
            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }
        
        val barData = BarData(dataSet)
        binding.readingFrequencyChart.data = barData
        
        // Set X axis labels
        binding.readingFrequencyChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(data.keys.sorted())
            labelRotationAngle = 45f
            labelCount = data.size
        }
        
        binding.readingFrequencyChart.invalidate()
    }
    
    private fun updateMostCommonCardsSection(cards: List<CardFrequency>) {
        binding.mostCommonCardsContainer.removeAllViews()
        
        for (cardFreq in cards) {
            val cardView = layoutInflater.inflate(
                R.layout.item_common_card,
                binding.mostCommonCardsContainer,
                false
            )
            
            val cardImage = cardView.findViewById<ImageView>(R.id.cardImage)
            val cardName = cardView.findViewById<TextView>(R.id.cardName)
            val cardFrequency = cardView.findViewById<TextView>(R.id.cardFrequency)
            
            // Load card image
            Glide.with(requireContext())
                .load(cardFreq.card.imageUrl)
                .into(cardImage)
            
            cardName.text = cardFreq.card.name
            cardFrequency.text = getString(R.string.card_frequency_format, cardFreq.frequency)
            
            binding.mostCommonCardsContainer.addView(cardView)
        }
    }
    
    private fun updateReadingStatsSection(stats: ReadingStats) {
        binding.totalReadingsValue.text = stats.totalReadings.toString()
        binding.averageCardsValue.text = String.format("%.1f", stats.averageCardsPerReading)
        binding.mostUsedSpreadValue.text = stats.mostUsedSpread ?: "N/A"
        binding.mostUsedDeckValue.text = stats.mostUsedDeck ?: "N/A"
        binding.readingStreakValue.text = stats.currentStreak.toString()
        binding.longestStreakValue.text = stats.longestStreak.toString()
    }
}
```

### 2. Reading Analytics ViewModel

```kotlin
class ReadingAnalyticsViewModel : ViewModel() {
    private val readingRepository = ReadingRepository()
    
    private val _cardFrequencyData = MutableLiveData<Map<String, Int>>()
    val cardFrequencyData: LiveData<Map<String, Int>> = _cardFrequencyData
    
    private val _suitDistributionData = MutableLiveData<Map<String, Int>>()
    val suitDistributionData: LiveData<Map<String, Int>> = _suitDistributionData
    
    private val _readingFrequencyData = MutableLiveData<Map<String, Int>>()
    val readingFrequencyData: LiveData<Map<String, Int>> = _readingFrequencyData
    
    private val _mostCommonCards = MutableLiveData<List<CardFrequency>>()
    val mostCommonCards: LiveData<List<CardFrequency>> = _mostCommonCards
    
    private val _readingStats = MutableLiveData<ReadingStats>()
    val readingStats: LiveData<ReadingStats> = _readingStats
    
    init {
        loadAnalyticsData()
    }
    
    private fun loadAnalyticsData() {
        viewModelScope.launch {
            // Get all readings with cards
            val allReadings = readingRepository.getAllReadingsWithCards()
            
            // Calculate card frequency
            calculateCardFrequency(allReadings)
            
            // Calculate suit distribution
            calculateSuitDistribution(allReadings)
            
            // Calculate reading frequency
            calculateReadingFrequency(allReadings)
            
            // Get most common cards
            findMostCommonCards(allReadings)
            
            // Calculate reading stats
            calculateReadingStats(allReadings)
        }
    }
    
    private fun calculateCardFrequency(readings: List<ReadingWithDetails>) {
        val cardFrequency = mutableMapOf<String, Int>()
        
        for (reading in readings) {
            for (card in reading.cards) {
                val cardName = card.card.name
                cardFrequency[cardName] = (cardFrequency[cardName] ?: 0) + 1
            }
        }
        
        _cardFrequencyData.value = cardFrequency
    }
    
    private fun calculateSuitDistribution(readings: List<ReadingWithDetails>) {
        val suitCounts = mutableMapOf(
            "Wands" to 0,
            "Cups" to 0,
            "Swords" to 0,
            "Pentacles" to 0,
            "Major Arcana" to 0
        )
        
        var totalCards = 0
        
        for (reading in readings) {
            for (card in reading.cards) {
                val suit = when {
                    card.card.arcanaType == "Major" -> "Major Arcana"
                    else -> card.card.suit
                }
                
                if (suit != null) {
                    suitCounts[suit] = (suitCounts[suit] ?: 0) + 1
                    totalCards++
                }
            }
        }
        
        _suitDistributionData.value = suitCounts
    }
    
    private fun calculateReadingFrequency(readings: List<ReadingWithDetails>) {
        val readingsByMonth = mutableMapOf<String, Int>()
        
        // Get date range (last 12 months)
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -11) // Go back 11 months for a total of 12 months
        val startDate = calendar.timeInMillis
        
        // Initialize all months with zero readings
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        calendar.timeInMillis = startDate
        while (calendar.timeInMillis <= endDate) {
            val monthKey = dateFormat.format(calendar.time)
            readingsByMonth[monthKey] = 0
            calendar.add(Calendar.MONTH, 1)
        }
        
        // Count readings by month
        for (reading in readings) {
            val readingDate = Date(reading.reading.createdAt)
            val monthKey = dateFormat.format(readingDate)
            
            if (reading.reading.createdAt >= startDate && reading.reading.createdAt <= endDate) {
                readingsByMonth[monthKey] = (readingsByMonth[monthKey] ?: 0) + 1
            }
        }
        
        _readingFrequencyData.value = readingsByMonth
    }
    
    private fun findMostCommonCards(readings: List<ReadingWithDetails>) {
        val cardFrequency = mutableMapOf<Card, Int>()
        
        for (reading in readings) {
            for (readingCard in reading.cards) {
                val card = readingCard.card
                cardFrequency[card] = (cardFrequency[card] ?: 0) + 1
            }
        }
        
        val mostCommon = cardFrequency.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { CardFrequency(it.key, it.value) }
        
        _mostCommonCards.value = mostCommon
    }
    
    private fun calculateReadingStats(readings: List<ReadingWithDetails>) {
        // Total readings
        val totalReadings = readings.size
        
        // Average cards per reading
        val totalCards = readings.sumOf { it.cards.size }
        val averageCards = if (totalReadings > 0) totalCards.toFloat() / totalReadings else 0f
        
        // Most used spread
        val spreadCounts = mutableMapOf<String, Int>()
        for (reading in readings) {
            val spreadName = reading.spread.name
            spreadCounts[spreadName] = (spreadCounts[spreadName] ?: 0) + 1
        }
        val mostUsedSpread = spreadCounts.entries.maxByOrNull { it.value }?.key
        
        // Most used deck
        val deckCounts = mutableMapOf<String, Int>()
        for (reading in readings) {
            val deckName = reading.deck.name
            deckCounts[deckName] = (deckCounts[deckName] ?: 0) + 1
        }
        val mostUsedDeck = deckCounts.entries.maxByOrNull { it.value }?.key
        
        // Reading streaks
        val (currentStreak, longestStreak) = calculateReadingStreaks(readings)
        
        _readingStats.value = ReadingStats(
            totalReadings = totalReadings,
            averageCardsPerReading = averageCards,
            mostUsedSpread = mostUsedSpread,
            mostUsedDeck = mostUsedDeck,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }
    
    private fun calculateReadingStreaks(readings: List<ReadingWithDetails>): Pair<Int, Int> {
        if (readings.isEmpty()) return Pair(0, 0)
        
        // Sort readings by date (newest first)
        val sortedReadings = readings.sortedByDescending { it.reading.createdAt }
        
        // Calculate current streak
        var currentStreak = 0
        val calendar = Calendar.getInstance()
        val today = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        var checkDate = today
        
        // Check if there's a reading today
        val hasReadingToday = sortedReadings.any { 
            val readingCal = Calendar.getInstance().apply { timeInMillis = it.reading.createdAt }
            val readingDay = readingCal.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            readingDay == today
        }
        
        if (hasReadingToday) {
            currentStreak = 1
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDate = calendar.timeInMillis
        }
        
        // Check previous days
        while (true) {
            val hasReadingOnDate = sortedReadings.any { 
                val readingCal = Calendar.getInstance().apply { timeInMillis = it.reading.createdAt }
                val readingDay = readingCal.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                readingDay == checkDate
            }
            
            if (hasReadingOnDate) {
                currentStreak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                checkDate = calendar.timeInMillis
            } else {
                break
            }
        }
        
        // Calculate longest streak
        var longestStreak = 0
        var currentLongestStreak = 0
        
        // Group readings by day
        val readingsByDay = sortedReadings.groupBy { reading ->
            val cal = Calendar.getInstance().apply { timeInMillis = reading.reading.createdAt }
            cal.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }.keys.sorted()
        
        if (readingsByDay.isNotEmpty()) {
            var prevDay = readingsByDay[0]
            currentLongestStreak = 1
            
            for (i in 1 until readingsByDay.size) {
                val currentDay = readingsByDay[i]
                
                val prevCal = Calendar.getInstance().apply { timeInMillis = prevDay }
                prevCal.add(Calendar.DAY_OF_YEAR, 1)
                val expectedNextDay = prevCal.timeInMillis
                
                if (currentDay == expectedNextDay) {
                    // Consecutive day
                    currentLongestStreak++
                } else {
                    // Streak broken
                    longestStreak = max(longestStreak, currentLongestStreak)
                    currentLongestStreak = 1
                }
                
                prevDay = currentDay
            }
            
            longestStreak = max(longestStreak, currentLongestStreak)
        }
        
        return Pair(currentStreak, longestStreak)
    }
}

data class CardFrequency(
    val card: Card,
    val frequency: Int
)

data class ReadingStats(
    val totalReadings: Int,
    val averageCardsPerReading: Float,
    val mostUsedSpread: String?,
    val mostUsedDeck: String?,
    val currentStreak: Int,
    val longestStreak: Int
)
```

## Reading Export and Sharing

### 1. PDF Export Functionality

```kotlin
class ReadingPdfExporter(private val context: Context) {
    
    fun exportReadingToPdf(reading: ReadingWithDetails, notes: List<Note>): File? {
        try {
            // Create PDF document
            val document = PdfDocument()
            
            // Create reading page
            val readingPage = createReadingPage(document, reading)
            document.finishPage(readingPage)
            
            // Create notes page if there are notes
            if (notes.isNotEmpty()) {
                val notesPage = createNotesPage(document, notes)
                document.finishPage(notesPage)
            }
            
            // Save PDF to file
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "tarot_reading_${reading.reading.id}_${System.currentTimeMillis()}.pdf"
            )
            
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            
            document.close()
            return file
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun createReadingPage(document: PdfDocument, reading: ReadingWithDetails): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(1200, 1800, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        
        // Fill background
        canvas.drawColor(Color.rgb(25, 25, 40))
        
        // Draw title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(reading.reading.title, 600f, 100f, titlePaint)
        
        // Draw spread name
        val spreadNamePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(reading.spread.name, 600f, 160f, spreadNamePaint)
        
        // Draw date
        val datePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            formatDate(reading.reading.createdAt),
            600f,
            210f,
            datePaint
        )
        
        // Draw cards
        drawCards(canvas, reading)
        
        // Draw eigenvalue
        val eigenvaluePaint = Paint().apply {
            color = Color.YELLOW
            textSize = 36f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}",
            600f,
            1100f,
            eigenvaluePaint
        )
        
        // Draw interpretation
        drawInterpretation(canvas, reading.reading.interpretation)
        
        // Draw footer
        val footerPaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Created with Tarot Reading App • ${formatDate(System.currentTimeMillis())}",
            600f,
            1750f,
            footerPaint
        )
        
        return page
    }
    
    private fun drawCards(canvas: Canvas, reading: ReadingWithDetails) {
        val cardWidth = 200f
        val cardHeight = 350f
        
        // Calculate positions based on spread layout
        val cardPositions = mutableListOf<RectF>()
        for (position in reading.spread.positions) {
            val x = 600f + (position.xPosition - 0.5f) * 800f
            val y = 600f + (position.yPosition - 0.5f) * 600f
            
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            cardPositions.add(rect)
        }
        
        // Draw cards on canvas
        for (i in reading.cards.indices) {
            val readingCard = reading.cards[i]
            val rect = cardPositions.getOrNull(i) ?: continue
            
            // Load card image
            val cardBitmap = loadCardBitmap(readingCard.card.imageUrl)
            if (cardBitmap != null) {
                // Apply rotation if card is reversed
                if (readingCard.isReversed) {
                    val matrix = Matrix()
                    matrix.setRotate(180f, cardBitmap.width / 2f, cardBitmap.height / 2f)
                    
                    val rotatedBitmap = Bitmap.createBitmap(
                        cardBitmap,
                        0,
                        0,
                        cardBitmap.width,
                        cardBitmap.height,
                        matrix,
                        true
                    )
                    
                    canvas.drawBitmap(rotatedBitmap, null, rect, null)
                } else {
                    canvas.drawBitmap(cardBitmap, null, rect, null)
                }
            } else {
                // Draw placeholder if image not loaded
                val paint = Paint().apply {
                    color = Color.GRAY
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(rect, 8f, 8f, paint)
            }
            
            // Draw position name
            val positionPaint = Paint().apply {
                color = Color.WHITE
                textSize = 24f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                readingCard.position.name,
                rect.centerX(),
                rect.bottom + 30f,
                positionPaint
            )
        }
    }
    
    private fun drawInterpretation(canvas: Canvas, interpretation: String) {
        val interpretationPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.LEFT
        }
        
        val textPaint = TextPaint(interpretationPaint)
        
        val textLayout = StaticLayout.Builder.obtain(
            interpretation,
            0,
            interpretation.length,
            textPaint,
            1000
        ).build()
        
        canvas.save()
        canvas.translate(100f, 1200f)
        textLayout.draw(canvas)
        canvas.restore()
    }
    
    private fun createNotesPage(document: PdfDocument, notes: List<Note>): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(1200, 1800, 2).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        
        // Fill background
        canvas.drawColor(Color.rgb(25, 25, 40))
        
        // Draw title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Reading Notes", 600f, 100f, titlePaint)
        
        // Draw notes
        val notePaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.LEFT
        }
        
        val dateTimePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 24f
            textAlign = Paint.Align.LEFT
        }
        
        var yPosition = 200f
        
        for (note in notes.sortedBy { it.createdAt }) {
            // Draw date
            canvas.drawText(
                formatDate(note.createdAt),
                100f,
                yPosition,
                dateTimePaint
            )
            
            yPosition += 40f
            
            // Draw note content
            val textPaint = TextPaint(notePaint)
            val textLayout = StaticLayout.Builder.obtain(
                note.content,
                0,
                note.content.length,
                textPaint,
                1000
            ).build()
            
            canvas.save()
            canvas.translate(100f, yPosition)
            textLayout.draw(canvas)
            canvas.restore()
            
            yPosition += textLayout.height + 60f
            
            // Draw separator line
            val linePaint = Paint().apply {
                color = Color.GRAY
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            
            canvas.drawLine(100f, yPosition - 30f, 1100f, yPosition - 30f, linePaint)
        }
        
        return page
    }
    
    private fun loadCardBitmap(imageUrl: String): Bitmap? {
        try {
            return Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}
```

### 2. Reading Sharing Functionality

```kotlin
class ReadingSharingManager(private val context: Context) {
    
    fun shareReadingAsImage(reading: ReadingWithDetails, callback: (Uri?) -> Unit) {
        generateReadingImage(reading) { bitmap ->
            if (bitmap != null) {
                // Save bitmap to cache directory
                val file = File(context.cacheDir, "reading_${reading.reading.id}_${System.currentTimeMillis()}.jpg")
                try {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    
                    // Create content URI
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    
                    callback(uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }
    
    fun getShareIntent(reading: ReadingWithDetails, imageUri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_SUBJECT, reading.reading.title)
            putExtra(Intent.EXTRA_TEXT, generateShareText(reading))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    
    private fun generateShareText(reading: ReadingWithDetails): String {
        return """
            ${reading.reading.title}
            
            Spread: ${reading.spread.name}
            Date: ${formatDate(reading.reading.createdAt)}
            
            ${reading.reading.interpretation.take(300)}...
            
            Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}
            
            Created with Tarot Reading App
        """.trimIndent()
    }
    
    private fun generateReadingImage(reading: ReadingWithDetails, callback: (Bitmap?) -> Unit) {
        try {
            // Create bitmap with appropriate size
            val bitmap = Bitmap.createBitmap(1200, 1800, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // Fill background
            canvas.drawColor(Color.rgb(25, 25, 40))
            
            // Draw title
            val titlePaint = Paint().apply {
                color = Color.WHITE
                textSize = 60f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(reading.reading.title, 600f, 100f, titlePaint)
            
            // Draw spread name
            val spreadNamePaint = Paint().apply {
                color = Color.LTGRAY
                textSize = 40f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(reading.spread.name, 600f, 160f, spreadNamePaint)
            
            // Draw date
            val datePaint = Paint().apply {
                color = Color.LTGRAY
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                formatDate(reading.reading.createdAt),
                600f,
                210f,
                datePaint
            )
            
            // Draw cards
            drawCards(canvas, reading) {
                // After cards are drawn, draw the rest of the content
                
                // Draw eigenvalue
                val eigenvaluePaint = Paint().apply {
                    color = Color.YELLOW
                    textSize = 36f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(
                    "Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}",
                    600f,
                    1100f,
                    eigenvaluePaint
                )
                
                // Draw interpretation (truncated)
                val interpretationPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 30f
                    textAlign = Paint.Align.LEFT
                }
                
                val interpretation = reading.reading.interpretation
                val truncatedInterpretation = if (interpretation.length > 300) {
                    interpretation.substring(0, 300) + "..."
                } else {
                    interpretation
                }
                
                val textLayout = StaticLayout.Builder.obtain(
                    truncatedInterpretation,
                    0,
                    truncatedInterpretation.length,
                    TextPaint(interpretationPaint),
                    1000
                ).build()
                
                canvas.save()
                canvas.translate(100f, 1200f)
                textLayout.draw(canvas)
                canvas.restore()
                
                // Draw app name and date at bottom
                val footerPaint = Paint().apply {
                    color = Color.LTGRAY
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(
                    "Created with Tarot Reading App • ${formatDate(System.currentTimeMillis())}",
                    600f,
                    1750f,
                    footerPaint
                )
                
                callback(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }
    
    private fun drawCards(canvas: Canvas, reading: ReadingWithDetails, onComplete: () -> Unit) {
        val cardWidth = 200f
        val cardHeight = 350f
        
        // Calculate positions based on spread layout
        val cardPositions = mutableListOf<RectF>()
        for (position in reading.spread.positions) {
            val x = 600f + (position.xPosition - 0.5f) * 800f
            val y = 600f + (position.yPosition - 0.5f) * 600f
            
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            cardPositions.add(rect)
        }
        
        // Load all card bitmaps asynchronously
        val cardBitmaps = mutableMapOf<Long, Bitmap>()
        val totalCards = reading.cards.size
        var loadedCards = 0
        
        for (readingCard in reading.cards) {
            Glide.with(context)
                .asBitmap()
                .load(readingCard.card.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        cardBitmaps[readingCard.card.id] = resource
                        loadedCards++
                        
                        if (loadedCards == totalCards) {
                            // All card images loaded, draw them
                            drawLoadedCards(canvas, reading, cardPositions, cardBitmaps)
                            onComplete()
                        }
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Not used
                    }
                    
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        loadedCards++
                        
                        if (loadedCards == totalCards) {
                            // All card images loaded (or failed), draw them
                            drawLoadedCards(canvas, reading, cardPositions, cardBitmaps)
                            onComplete()
                        }
                    }
                })
        }
        
        // If there are no cards, call onComplete immediately
        if (totalCards == 0) {
            onComplete()
        }
    }
    
    private fun drawLoadedCards(
        canvas: Canvas,
        reading: ReadingWithDetails,
        cardPositions: List<RectF>,
        cardBitmaps: Map<Long, Bitmap>
    ) {
        for (i in reading.cards.indices) {
            val readingCard = reading.cards[i]
            val rect = cardPositions.getOrNull(i) ?: continue
            
            // Get card bitmap
            val cardBitmap = cardBitmaps[readingCard.card.id]
            if (cardBitmap != null) {
                // Apply rotation if card is reversed
                if (readingCard.isReversed) {
                    val matrix = Matrix()
                    matrix.setRotate(180f, cardBitmap.width / 2f, cardBitmap.height / 2f)
                    
                    val rotatedBitmap = Bitmap.createBitmap(
                        cardBitmap,
                        0,
                        0,
                        cardBitmap.width,
                        cardBitmap.height,
                        matrix,
                        true
                    )
                    
                    canvas.drawBitmap(rotatedBitmap, null, rect, null)
                } else {
                    canvas.drawBitmap(cardBitmap, null, rect, null)
                }
            } else {
                // Draw placeholder if image not loaded
                val paint = Paint().apply {
                    color = Color.GRAY
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(rect, 8f, 8f, paint)
            }
            
            // Draw position name
            val positionPaint = Paint().apply {
                color = Color.WHITE
                textSize = 24f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                readingCard.position.name,
                rect.centerX(),
                rect.bottom + 30f,
                positionPaint
            )
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
```