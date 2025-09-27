# Tarot Card Dictionary Implementation

## Overview

The Tarot Card Dictionary feature provides users with a comprehensive reference for all Tarot cards, including detailed meanings, interpretations, and visual representations. This document outlines the implementation details for creating an intuitive, informative, and visually appealing card dictionary experience.

## User Flow

1. **Dictionary Overview**
   - User accesses the card dictionary section
   - User selects a Tarot deck to explore
   - User views a grid of all cards in the selected deck

2. **Card Filtering and Searching**
   - User filters cards by arcana type, suit, or other attributes
   - User searches for specific cards by name or keywords

3. **Card Detail View**
   - User selects a specific card to view detailed information
   - User explores card meanings, symbolism, and associations

4. **Learning and Reference**
   - User accesses educational content about card interpretations
   - User can bookmark favorite cards for quick reference

## Implementation Details

### 1. Card Dictionary Fragment

```kotlin
class CardDictionaryFragment : Fragment() {
    private lateinit var viewModel: CardDictionaryViewModel
    private lateinit var binding: FragmentCardDictionaryBinding
    private lateinit var cardAdapter: CardDictionaryAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(CardDictionaryViewModel::class.java)
        
        setupDeckSelector()
        setupCardGrid()
        setupFilters()
        setupSearch()
        observeViewModel()
    }
    
    private fun setupDeckSelector() {
        binding.deckSelector.setOnClickListener {
            showDeckSelectionDialog()
        }
    }
    
    private fun setupCardGrid() {
        cardAdapter = CardDictionaryAdapter { card ->
            findNavController().navigate(
                CardDictionaryFragmentDirections.actionToCardDetail(card.id)
            )
        }
        
        binding.cardGrid.apply {
            adapter = cardAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(CardGridItemDecoration(3, dpToPx(8), true))
        }
    }
    
    private fun setupFilters() {
        // Major/Minor Arcana filter
        binding.arcanaFilterGroup.setOnCheckedChangeListener { _, checkedId ->
            val arcanaType = when (checkedId) {
                R.id.majorArcanaFilter -> "Major"
                R.id.minorArcanaFilter -> "Minor"
                else -> null
            }
            viewModel.setArcanaTypeFilter(arcanaType)
        }
        
        // Suit filters
        binding.suitFilterGroup.setOnCheckedChangeListener { _, checkedId ->
            val suit = when (checkedId) {
                R.id.wandsFilter -> "Wands"
                R.id.cupsFilter -> "Cups"
                R.id.swordsFilter -> "Swords"
                R.id.pentaclesFilter -> "Pentacles"
                else -> null
            }
            viewModel.setSuitFilter(suit)
        }
        
        // Clear filters button
        binding.clearFiltersButton.setOnClickListener {
            binding.arcanaFilterGroup.clearCheck()
            binding.suitFilterGroup.clearCheck()
            viewModel.clearFilters()
        }
    }
    
    private fun setupSearch() {
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
    }
    
    private fun observeViewModel() {
        viewModel.selectedDeck.observe(viewLifecycleOwner) { deck ->
            binding.deckName.text = deck.name
            
            // Load deck image
            Glide.with(requireContext())
                .load(deck.imageUrl)
                .transform(RoundedCorners(dpToPx(8)))
                .into(binding.deckImage)
        }
        
        viewModel.filteredCards.observe(viewLifecycleOwner) { cards ->
            cardAdapter.submitList(cards)
            binding.emptyStateGroup.isVisible = cards.isEmpty()
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        
        viewModel.activeFilters.observe(viewLifecycleOwner) { filters ->
            updateFilterChips(filters)
        }
    }
    
    private fun updateFilterChips(filters: CardFilters) {
        binding.filterChipGroup.removeAllViews()
        
        // Arcana type chip
        filters.arcanaType?.let { arcanaType ->
            addFilterChip("Arcana: $arcanaType") {
                binding.arcanaFilterGroup.clearCheck()
                viewModel.setArcanaTypeFilter(null)
            }
        }
        
        // Suit chip
        filters.suit?.let { suit ->
            addFilterChip("Suit: $suit") {
                binding.suitFilterGroup.clearCheck()
                viewModel.setSuitFilter(null)
            }
        }
        
        // Search query chip
        if (filters.searchQuery.isNotEmpty()) {
            addFilterChip("Search: ${filters.searchQuery}") {
                binding.searchView.setQuery("", false)
                viewModel.setSearchQuery("")
            }
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
    
    private fun showDeckSelectionDialog() {
        val decks = viewModel.getAllDecks()
        val deckNames = decks.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_deck)
            .setItems(deckNames) { _, which ->
                viewModel.selectDeck(decks[which])
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
```

### 2. Card Dictionary ViewModel

```kotlin
class CardDictionaryViewModel : ViewModel() {
    private val deckRepository = DeckRepository()
    private val cardRepository = CardRepository()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _selectedDeck = MutableLiveData<Deck>()
    val selectedDeck: LiveData<Deck> = _selectedDeck
    
    private val _allCards = MutableLiveData<List<Card>>()
    
    private val _activeFilters = MutableLiveData(CardFilters())
    val activeFilters: LiveData<CardFilters> = _activeFilters
    
    private val _filteredCards = MediatorLiveData<List<Card>>()
    val filteredCards: LiveData<List<Card>> = _filteredCards
    
    private var allDecks: List<Deck> = emptyList()
    
    init {
        _filteredCards.addSource(_allCards) { cards ->
            _filteredCards.value = applyFilters(cards, _activeFilters.value ?: CardFilters())
        }
        
        _filteredCards.addSource(_activeFilters) { filters ->
            _filteredCards.value = applyFilters(_allCards.value ?: emptyList(), filters)
        }
        
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Load all decks
            allDecks = deckRepository.getAllDecks()
            
            // Select default deck
            val defaultDeckId = userPreferencesRepository.getDefaultDeckId()
            val defaultDeck = if (defaultDeckId != null) {
                allDecks.find { it.id == defaultDeckId }
            } else {
                allDecks.firstOrNull()
            }
            
            defaultDeck?.let { selectDeck(it) }
            
            _isLoading.value = false
        }
    }
    
    fun selectDeck(deck: Deck) {
        if (_selectedDeck.value?.id != deck.id) {
            _selectedDeck.value = deck
            loadCardsForDeck(deck.id)
        }
    }
    
    private fun loadCardsForDeck(deckId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val cards = cardRepository.getCardsForDeck(deckId)
            _allCards.value = cards
            
            _isLoading.value = false
        }
    }
    
    fun setArcanaTypeFilter(arcanaType: String?) {
        _activeFilters.value = _activeFilters.value?.copy(arcanaType = arcanaType)
    }
    
    fun setSuitFilter(suit: String?) {
        _activeFilters.value = _activeFilters.value?.copy(suit = suit)
    }
    
    fun setSearchQuery(query: String) {
        _activeFilters.value = _activeFilters.value?.copy(searchQuery = query)
    }
    
    fun clearFilters() {
        _activeFilters.value = CardFilters()
    }
    
    private fun applyFilters(cards: List<Card>, filters: CardFilters): List<Card> {
        return cards.filter { card ->
            // Apply arcana type filter
            val matchesArcanaType = filters.arcanaType == null || card.arcanaType == filters.arcanaType
            
            // Apply suit filter
            val matchesSuit = filters.suit == null || card.suit == filters.suit
            
            // Apply search query
            val matchesSearch = filters.searchQuery.isEmpty() || 
                card.name.contains(filters.searchQuery, ignoreCase = true) ||
                card.keywords.any { it.keyword.contains(filters.searchQuery, ignoreCase = true) }
            
            matchesArcanaType && matchesSuit && matchesSearch
        }
    }
    
    fun getAllDecks(): List<Deck> = allDecks
}

data class CardFilters(
    val arcanaType: String? = null,
    val suit: String? = null,
    val searchQuery: String = ""
)
```

### 3. Card Dictionary Adapter

```kotlin
class CardDictionaryAdapter(
    private val onCardClick: (Card) -> Unit
) : ListAdapter<Card, CardViewHolder>(CardDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardDictionaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding, onCardClick)
    }
    
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CardViewHolder(
    private val binding: ItemCardDictionaryBinding,
    private val onCardClick: (Card) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(card: Card) {
        binding.cardName.text = card.name
        
        // Set card number and suit
        val cardInfo = when (card.arcanaType) {
            "Major" -> "Major Arcana • ${card.number}"
            "Minor" -> "${card.suit} • ${getCardRank(card.number)}"
            else -> ""
        }
        binding.cardInfo.text = cardInfo
        
        // Load card image
        Glide.with(binding.root)
            .load(card.imageUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .into(binding.cardImage)
        
        // Set click listener
        binding.root.setOnClickListener {
            onCardClick(card)
        }
    }
    
    private fun getCardRank(number: Int): String {
        return when (number) {
            1 -> "Ace"
            11 -> "Page"
            12 -> "Knight"
            13 -> "Queen"
            14 -> "King"
            else -> number.toString()
        }
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * binding.root.resources.displayMetrics.density).toInt()
    }
}

class CardDiffCallback : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem == newItem
    }
}

class CardGridItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {
    
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}
```

### 4. Card Detail Fragment

```kotlin
class CardDetailFragment : Fragment() {
    private lateinit var viewModel: CardDetailViewModel
    private lateinit var binding: FragmentCardDetailBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val cardId = args.cardId
        
        viewModel = ViewModelProvider(
            this,
            CardDetailViewModelFactory(cardId)
        ).get(CardDetailViewModel::class.java)
        
        setupTabs()
        setupFavoriteButton()
        observeViewModel()
    }
    
    private fun setupTabs() {
        val tabAdapter = CardDetailTabAdapter(this)
        binding.viewPager.adapter = tabAdapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.overview)
                1 -> getString(R.string.upright)
                2 -> getString(R.string.reversed)
                3 -> getString(R.string.symbolism)
                else -> null
            }
        }.attach()
    }
    
    private fun setupFavoriteButton() {
        binding.favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
    
    private fun observeViewModel() {
        viewModel.card.observe(viewLifecycleOwner) { card ->
            updateUI(card)
        }
        
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }
    }
    
    private fun updateUI(card: Card) {
        binding.cardName.text = card.name
        
        // Set card number and suit
        val cardInfo = when (card.arcanaType) {
            "Major" -> "Major Arcana • ${card.number}"
            "Minor" -> "${card.suit} • ${getCardRank(card.number)}"
            else -> ""
        }
        binding.cardInfo.text = cardInfo
        
        // Load card image
        Glide.with(requireContext())
            .load(card.imageUrl)
            .into(binding.cardImage)
        
        // Set card associations
        binding.elementValue.text = card.element ?: "N/A"
        binding.zodiacValue.text = card.zodiac ?: "N/A"
        binding.numerologyValue.text = card.numerology ?: "N/A"
        
        // Set keywords
        updateKeywords(card)
    }
    
    private fun updateKeywords(card: Card) {
        // Upright keywords
        binding.uprightKeywordsContainer.removeAllViews()
        card.keywords.filter { !it.isReversed }.forEach { keyword ->
            val chip = Chip(requireContext()).apply {
                text = keyword.keyword
                isClickable = false
            }
            binding.uprightKeywordsContainer.addView(chip)
        }
        
        // Reversed keywords
        binding.reversedKeywordsContainer.removeAllViews()
        card.keywords.filter { it.isReversed }.forEach { keyword ->
            val chip = Chip(requireContext()).apply {
                text = keyword.keyword
                isClickable = false
            }
            binding.reversedKeywordsContainer.addView(chip)
        }
    }
    
    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_outline
        )
    }
    
    private fun getCardRank(number: Int): String {
        return when (number) {
            1 -> "Ace"
            11 -> "Page"
            12 -> "Knight"
            13 -> "Queen"
            14 -> "King"
            else -> number.toString()
        }
    }
}
```

### 5. Card Detail ViewModel

```kotlin
class CardDetailViewModel(
    private val cardId: Long
) : ViewModel() {
    private val cardRepository = CardRepository()
    private val favoriteRepository = FavoriteRepository()
    
    private val _card = MutableLiveData<Card>()
    val card: LiveData<Card> = _card
    
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    
    init {
        loadCard()
        checkIfFavorite()
    }
    
    private fun loadCard() {
        viewModelScope.launch {
            val card = cardRepository.getCardById(cardId)
            _card.value = card
        }
    }
    
    private fun checkIfFavorite() {
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isCardFavorite(cardId)
            _isFavorite.value = isFavorite
        }
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentValue = _isFavorite.value ?: false
            
            if (currentValue) {
                favoriteRepository.removeCardFromFavorites(cardId)
            } else {
                favoriteRepository.addCardToFavorites(cardId)
            }
            
            _isFavorite.value = !currentValue
        }
    }
}

class CardDetailViewModelFactory(private val cardId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardDetailViewModel(cardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

### 6. Card Detail Tab Adapter and Fragments

```kotlin
class CardDetailTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CardOverviewFragment()
            1 -> CardUprightMeaningFragment()
            2 -> CardReversedMeaningFragment()
            3 -> CardSymbolismFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}

class CardOverviewFragment : Fragment() {
    private lateinit var binding: FragmentCardOverviewBinding
    private lateinit var viewModel: CardDetailViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(CardDetailViewModel::class.java)
        
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.card.observe(viewLifecycleOwner) { card ->
            binding.overviewText.text = generateOverviewText(card)
        }
    }
    
    private fun generateOverviewText(card: Card): String {
        val stringBuilder = StringBuilder()
        
        // Add introduction based on arcana type
        if (card.arcanaType == "Major") {
            stringBuilder.append("${card.name} is a Major Arcana card, representing significant life events, lessons, or archetypal energies. ")
            stringBuilder.append("As the number ${card.number} in the Major Arcana sequence, it symbolizes a key stage in the Fool's journey through the Tarot. ")
        } else {
            stringBuilder.append("${card.name} is a ${getCardRank(card.number)} from the suit of ${card.suit} in the Minor Arcana. ")
            stringBuilder.append("The ${card.suit} suit represents ${getSuitDomain(card.suit)}, and as a ${getCardRank(card.number)}, this card embodies ${getRankEnergy(card.number)}. ")
        }
        
        // Add elemental association
        card.element?.let {
            stringBuilder.append("This card is associated with the element of $it, which represents ${getElementMeaning(it)}. ")
        }
        
        // Add zodiac association
        card.zodiac?.let {
            stringBuilder.append("In astrology, ${card.name} connects to $it, bringing the qualities of ${getZodiacTraits(it)}. ")
        }
        
        // Add numerological significance
        card.numerology?.let {
            stringBuilder.append("Numerologically, this card resonates with the energy of $it, symbolizing ${getNumerologyMeaning(it)}. ")
        }
        
        // Add general meaning
        stringBuilder.append("\n\nIn readings, ${card.name} often appears when ${getGeneralMeaning(card)}. ")
        
        // Add advice
        stringBuilder.append("When this card appears in your reading, it may be suggesting that you ${getCardAdvice(card)}.")
        
        return stringBuilder.toString()
    }
    
    private fun getCardRank(number: Int): String {
        return when (number) {
            1 -> "Ace"
            11 -> "Page"
            12 -> "Knight"
            13 -> "Queen"
            14 -> "King"
            else -> number.toString()
        }
    }
    
    private fun getSuitDomain(suit: String?): String {
        return when (suit) {
            "Wands" -> "passion, creativity, and spiritual energy"
            "Cups" -> "emotions, relationships, and intuition"
            "Swords" -> "intellect, communication, and conflict"
            "Pentacles" -> "material matters, work, and physical resources"
            else -> "various aspects of life"
        }
    }
    
    private fun getRankEnergy(number: Int): String {
        return when (number) {
            1 -> "new beginnings and potential"
            2 -> "balance and duality"
            3 -> "creative expression and growth"
            4 -> "stability and foundation"
            5 -> "challenge and conflict"
            6 -> "harmony and cooperation"
            7 -> "reflection and assessment"
            8 -> "mastery and power"
            9 -> "completion and fulfillment"
            10 -> "culmination and transition"
            11 -> "learning and exploration"
            12 -> "action and movement"
            13 -> "nurturing and emotional intelligence"
            14 -> "authority and leadership"
            else -> "various energies"
        }
    }
    
    private fun getElementMeaning(element: String): String {
        return when (element) {
            "Fire" -> "passion, energy, and transformation"
            "Water" -> "emotions, intuition, and the subconscious"
            "Air" -> "intellect, communication, and mental clarity"
            "Earth" -> "practicality, stability, and material concerns"
            else -> "various aspects of life"
        }
    }
    
    private fun getZodiacTraits(zodiac: String): String {
        return when (zodiac) {
            "Aries" -> "courage, initiative, and assertiveness"
            "Taurus" -> "stability, determination, and sensuality"
            "Gemini" -> "adaptability, curiosity, and communication"
            "Cancer" -> "nurturing, intuition, and emotional depth"
            "Leo" -> "creativity, leadership, and self-expression"
            "Virgo" -> "analysis, practicality, and attention to detail"
            "Libra" -> "balance, harmony, and relationships"
            "Scorpio" -> "transformation, intensity, and depth"
            "Sagittarius" -> "exploration, optimism, and philosophy"
            "Capricorn" -> "ambition, discipline, and responsibility"
            "Aquarius" -> "innovation, independence, and humanitarianism"
            "Pisces" -> "compassion, spirituality, and imagination"
            else -> "various astrological qualities"
        }
    }
    
    private fun getNumerologyMeaning(numerology: String): String {
        return when (numerology) {
            "1" -> "new beginnings, independence, and leadership"
            "2" -> "partnership, duality, and balance"
            "3" -> "creativity, expression, and joy"
            "4" -> "stability, structure, and foundation"
            "5" -> "change, freedom, and adventure"
            "6" -> "harmony, responsibility, and nurturing"
            "7" -> "analysis, wisdom, and spirituality"
            "8" -> "power, abundance, and achievement"
            "9" -> "completion, humanitarianism, and wisdom"
            else -> "various numerological principles"
        }
    }
    
    private fun getGeneralMeaning(card: Card): String {
        // This would ideally come from the card data
        // For now, we'll return a placeholder based on the card's keywords
        val keywords = card.keywords.filter { !it.isReversed }.take(3).map { it.keyword }
        return if (keywords.isNotEmpty()) {
            "themes of ${keywords.joinToString(", ")} are present in your life"
        } else {
            "important energies are at play in your life"
        }
    }
    
    private fun getCardAdvice(card: Card): String {
        // This would ideally come from the card data
        // For now, we'll return a placeholder based on the card's keywords
        val keywords = card.keywords.filter { !it.isReversed }.take(2).map { it.keyword }
        return if (keywords.isNotEmpty()) {
            "consider embracing the qualities of ${keywords.joinToString(" and ")}"
        } else {
            "reflect on the energies this card represents"
        }
    }
}

class CardUprightMeaningFragment : Fragment() {
    private lateinit var binding: FragmentCardMeaningBinding
    private lateinit var viewModel: CardDetailViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(CardDetailViewModel::class.java)
        
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.card.observe(viewLifecycleOwner) { card ->
            binding.meaningText.text = card.uprightMeaning
            
            // Set keywords
            binding.keywordsContainer.removeAllViews()
            card.keywords.filter { !it.isReversed }.forEach { keyword ->
                val chip = Chip(requireContext()).apply {
                    text = keyword.keyword
                    isClickable = false
                }
                binding.keywordsContainer.addView(chip)
            }
        }
    }
}

class CardReversedMeaningFragment : Fragment() {
    private lateinit var binding: FragmentCardMeaningBinding
    private lateinit var viewModel: CardDetailViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(CardDetailViewModel::class.java)
        
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.card.observe(viewLifecycleOwner) { card ->
            binding.meaningText.text = card.reversedMeaning
            
            // Set keywords
            binding.keywordsContainer.removeAllViews()
            card.keywords.filter { it.isReversed }.forEach { keyword ->
                val chip = Chip(requireContext()).apply {
                    text = keyword.keyword
                    isClickable = false
                }
                binding.keywordsContainer.addView(chip)
            }
        }
    }
}

class CardSymbolismFragment : Fragment() {
    private lateinit var binding: FragmentCardSymbolismBinding
    private lateinit var viewModel: CardDetailViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireParentFragment()).get(CardDetailViewModel::class.java)
        
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.card.observe(viewLifecycleOwner) { card ->
            binding.symbolismText.text = card.symbolism ?: getString(R.string.no_symbolism_available)
            
            // Set up image with zoom capability
            Glide.with(requireContext())
                .load(card.imageUrl)
                .into(binding.cardImageLarge)
            
            setupZoomableImage()
        }
    }
    
    private fun setupZoomableImage() {
        val photoAttacher = PhotoViewAttacher(binding.cardImageLarge)
        photoAttacher.update()
    }
}
```

### 7. Card Favorite Repository

```kotlin
class FavoriteRepository {
    private val favoriteDao = TarotDatabase.getInstance().favoriteDao()
    
    suspend fun addCardToFavorites(cardId: Long) {
        val userId = userRepository.getCurrentUserId()
        val favorite = FavoriteCard(
            id = 0, // Room will generate ID
            userId = userId,
            cardId = cardId,
            createdAt = System.currentTimeMillis()
        )
        favoriteDao.insertFavorite(favorite)
    }
    
    suspend fun removeCardFromFavorites(cardId: Long) {
        val userId = userRepository.getCurrentUserId()
        favoriteDao.deleteFavoriteByUserAndCard(userId, cardId)
    }
    
    suspend fun isCardFavorite(cardId: Long): Boolean {
        val userId = userRepository.getCurrentUserId()
        return favoriteDao.getFavoriteByUserAndCard(userId, cardId) != null
    }
    
    suspend fun getFavoriteCards(): List<Card> {
        val userId = userRepository.getCurrentUserId()
        return favoriteDao.getFavoriteCardsByUser(userId)
    }
}
```

## Card Data Structure

### 1. Card Data Classes

```kotlin
data class Card(
    val id: Long,
    val deckId: Long,
    val name: String,
    val number: Int,
    val arcanaType: String, // "Major" or "Minor"
    val suit: String?, // null for Major Arcana
    val imageUrl: String,
    val keywords: List<CardKeyword>,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val element: String?,
    val zodiac: String?,
    val numerology: String?,
    val symbolism: String?
)

data class CardKeyword(
    val id: Long,
    val cardId: Long,
    val keyword: String,
    val isReversed: Boolean
)

data class FavoriteCard(
    val id: Long,
    val userId: String,
    val cardId: Long,
    val createdAt: Long
)
```

### 2. Card Database Schema

```kotlin
@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val name: String,
    val number: Int,
    val arcanaType: String,
    val suit: String?,
    val imagePath: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val element: String?,
    val zodiac: String?,
    val numerology: String?,
    val symbolism: String?
)

@Entity(tableName = "card_keywords")
data class CardKeywordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: Long,
    val keyword: String,
    val isReversed: Boolean
)

@Entity(
    tableName = "favorite_cards",
    indices = [Index(value = ["userId", "cardId"], unique = true)]
)
data class FavoriteCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val cardId: Long,
    val createdAt: Long
)
```

### 3. Card Data Access Objects

```kotlin
@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY arcanaType DESC, suit, number")
    suspend fun getCardsByDeck(deckId: Long): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Long): CardEntity?
    
    @Query("SELECT * FROM cards WHERE arcanaType = 'Major' AND number = :number LIMIT 1")
    suspend fun getMajorArcanaByNumber(number: Int): CardEntity?
    
    @Query("SELECT * FROM cards WHERE deckId = :deckId AND arcanaType = 'Major' AND number = :number LIMIT 1")
    suspend fun getMajorArcanaByDeckAndNumber(deckId: Long, number: Int): CardEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long
    
    @Update
    suspend fun updateCard(card: CardEntity)
    
    @Delete
    suspend fun deleteCard(card: CardEntity)
}

@Dao
interface CardKeywordDao {
    @Query("SELECT * FROM card_keywords WHERE cardId = :cardId")
    suspend fun getKeywordsForCard(cardId: Long): List<CardKeywordEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: CardKeywordEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywords(keywords: List<CardKeywordEntity>)
    
    @Delete
    suspend fun deleteKeyword(keyword: CardKeywordEntity)
    
    @Query("DELETE FROM card_keywords WHERE cardId = :cardId")
    suspend fun deleteKeywordsForCard(cardId: Long)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_cards WHERE userId = :userId")
    suspend fun getFavoritesByUser(userId: String): List<FavoriteCardEntity>
    
    @Query("SELECT c.* FROM cards c INNER JOIN favorite_cards f ON c.id = f.cardId WHERE f.userId = :userId")
    suspend fun getFavoriteCardsByUser(userId: String): List<CardEntity>
    
    @Query("SELECT * FROM favorite_cards WHERE userId = :userId AND cardId = :cardId LIMIT 1")
    suspend fun getFavoriteByUserAndCard(userId: String, cardId: Long): FavoriteCardEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteCardEntity): Long
    
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteCardEntity)
    
    @Query("DELETE FROM favorite_cards WHERE userId = :userId AND cardId = :cardId")
    suspend fun deleteFavoriteByUserAndCard(userId: String, cardId: Long)
}
```

## Card Learning Features

### 1. Card Learning Fragment

```kotlin
class CardLearningFragment : Fragment() {
    private lateinit var viewModel: CardLearningViewModel
    private lateinit var binding: FragmentCardLearningBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(CardLearningViewModel::class.java)
        
        setupLearningModules()
        observeViewModel()
    }
    
    private fun setupLearningModules() {
        // Major Arcana module
        binding.majorArcanaModule.setOnClickListener {
            findNavController().navigate(
                CardLearningFragmentDirections.actionToLearningModule("major_arcana")
            )
        }
        
        // Minor Arcana module
        binding.minorArcanaModule.setOnClickListener {
            findNavController().navigate(
                CardLearningFragmentDirections.actionToLearningModule("minor_arcana")
            )
        }
        
        // Suits module
        binding.suitsModule.setOnClickListener {
            findNavController().navigate(
                CardLearningFragmentDirections.actionToLearningModule("suits")
            )
        }
        
        // Court Cards module
        binding.courtCardsModule.setOnClickListener {
            findNavController().navigate(
                CardLearningFragmentDirections.actionToLearningModule("court_cards")
            )
        }
        
        // Numerology module
        binding.numerologyModule.setOnClickListener {
            findNavController().navigate(
                CardLearningFragmentDirections.actionToLearningModule("numerology")
            )
        }
        
        // Reversals module
        binding.reversalsModule.setOnClickListener {
            findNavController().navigate(
                CardLearningFragmentDirections.actionToLearningModule("reversals")
            )
        }
    }
    
    private fun observeViewModel() {
        viewModel.learningProgress.observe(viewLifecycleOwner) { progress ->
            updateProgressBars(progress)
        }
    }
    
    private fun updateProgressBars(progress: Map<String, Float>) {
        // Update Major Arcana progress
        binding.majorArcanaProgress.progress = (progress["major_arcana"] ?: 0f).toInt()
        binding.majorArcanaProgressText.text = "${(progress["major_arcana"] ?: 0f).toInt()}%"
        
        // Update Minor Arcana progress
        binding.minorArcanaProgress.progress = (progress["minor_arcana"] ?: 0f).toInt()
        binding.minorArcanaProgressText.text = "${(progress["minor_arcana"] ?: 0f).toInt()}%"
        
        // Update Suits progress
        binding.suitsProgress.progress = (progress["suits"] ?: 0f).toInt()
        binding.suitsProgressText.text = "${(progress["suits"] ?: 0f).toInt()}%"
        
        // Update Court Cards progress
        binding.courtCardsProgress.progress = (progress["court_cards"] ?: 0f).toInt()
        binding.courtCardsProgressText.text = "${(progress["court_cards"] ?: 0f).toInt()}%"
        
        // Update Numerology progress
        binding.numerologyProgress.progress = (progress["numerology"] ?: 0f).toInt()
        binding.numerologyProgressText.text = "${(progress["numerology"] ?: 0f).toInt()}%"
        
        // Update Reversals progress
        binding.reversalsProgress.progress = (progress["reversals"] ?: 0f).toInt()
        binding.reversalsProgressText.text = "${(progress["reversals"] ?: 0f).toInt()}%"
        
        // Update overall progress
        val overallProgress = progress.values.average().toFloat()
        binding.overallProgress.progress = overallProgress.toInt()
        binding.overallProgressText.text = "${overallProgress.toInt()}% Complete"
    }
}
```

### 2. Card Learning ViewModel

```kotlin
class CardLearningViewModel : ViewModel() {
    private val learningRepository = LearningRepository()
    
    private val _learningProgress = MutableLiveData<Map<String, Float>>()
    val learningProgress: LiveData<Map<String, Float>> = _learningProgress
    
    init {
        loadLearningProgress()
    }
    
    private fun loadLearningProgress() {
        viewModelScope.launch {
            val progress = learningRepository.getLearningProgress()
            _learningProgress.value = progress
        }
    }
}
```

### 3. Learning Module Fragment

```kotlin
class LearningModuleFragment : Fragment() {
    private lateinit var viewModel: LearningModuleViewModel
    private lateinit var binding: FragmentLearningModuleBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val moduleId = args.moduleId
        
        viewModel = ViewModelProvider(
            this,
            LearningModuleViewModelFactory(moduleId)
        ).get(LearningModuleViewModel::class.java)
        
        setupViewPager()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupViewPager() {
        binding.viewPager.apply {
            adapter = LearningContentAdapter(this@LearningModuleFragment)
            isUserInputEnabled = false // Disable swiping
        }
    }
    
    private fun setupButtons() {
        binding.previousButton.setOnClickListener {
            viewModel.navigateToPreviousPage()
        }
        
        binding.nextButton.setOnClickListener {
            viewModel.navigateToNextPage()
        }
        
        binding.completeButton.setOnClickListener {
            viewModel.markModuleAsCompleted()
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        viewModel.moduleTitle.observe(viewLifecycleOwner) { title ->
            binding.moduleTitle.text = title
        }
        
        viewModel.currentPageIndex.observe(viewLifecycleOwner) { index ->
            binding.viewPager.currentItem = index
            updateNavigationButtons(index)
        }
        
        viewModel.totalPages.observe(viewLifecycleOwner) { total ->
            binding.pageIndicator.text = getString(
                R.string.page_indicator_format,
                viewModel.currentPageIndex.value?.plus(1) ?: 1,
                total
            )
        }
        
        viewModel.learningContent.observe(viewLifecycleOwner) { content ->
            (binding.viewPager.adapter as LearningContentAdapter).setContent(content)
        }
    }
    
    private fun updateNavigationButtons(currentIndex: Int) {
        val totalPages = viewModel.totalPages.value ?: 0
        
        binding.previousButton.isEnabled = currentIndex > 0
        binding.nextButton.isEnabled = currentIndex < totalPages - 1
        binding.completeButton.isVisible = currentIndex == totalPages - 1
        
        binding.pageIndicator.text = getString(
            R.string.page_indicator_format,
            currentIndex + 1,
            totalPages
        )
    }
}
```

### 4. Learning Module ViewModel

```kotlin
class LearningModuleViewModel(
    private val moduleId: String
) : ViewModel() {
    private val learningRepository = LearningRepository()
    
    private val _moduleTitle = MutableLiveData<String>()
    val moduleTitle: LiveData<String> = _moduleTitle
    
    private val _currentPageIndex = MutableLiveData<Int>()
    val currentPageIndex: LiveData<Int> = _currentPageIndex
    
    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages
    
    private val _learningContent = MutableLiveData<List<LearningContent>>()
    val learningContent: LiveData<List<LearningContent>> = _learningContent
    
    init {
        loadModuleContent()
    }
    
    private fun loadModuleContent() {
        viewModelScope.launch {
            val module = learningRepository.getLearningModule(moduleId)
            
            _moduleTitle.value = module.title
            _learningContent.value = module.content
            _totalPages.value = module.content.size
            _currentPageIndex.value = 0
        }
    }
    
    fun navigateToPreviousPage() {
        val currentIndex = _currentPageIndex.value ?: 0
        if (currentIndex > 0) {
            _currentPageIndex.value = currentIndex - 1
        }
    }
    
    fun navigateToNextPage() {
        val currentIndex = _currentPageIndex.value ?: 0
        val totalPages = _totalPages.value ?: 0
        
        if (currentIndex < totalPages - 1) {
            _currentPageIndex.value = currentIndex + 1
        }
    }
    
    fun markModuleAsCompleted() {
        viewModelScope.launch {
            learningRepository.markModuleAsCompleted(moduleId)
        }
    }
}

class LearningModuleViewModelFactory(private val moduleId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningModuleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearningModuleViewModel(moduleId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

### 5. Learning Content Adapter

```kotlin
class LearningContentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private var contentList: List<LearningContent> = emptyList()
    
    override fun getItemCount(): Int = contentList.size
    
    override fun createFragment(position: Int): Fragment {
        val content = contentList.getOrNull(position) ?: return EmptyContentFragment()
        
        return when (content.type) {
            ContentType.TEXT -> TextContentFragment.newInstance(content.title, content.text)
            ContentType.IMAGE_TEXT -> ImageTextContentFragment.newInstance(content.title, content.text, content.imageUrl)
            ContentType.CARD_GRID -> CardGridContentFragment.newInstance(content.title, content.cardIds)
            ContentType.QUIZ -> QuizContentFragment.newInstance(content.title, content.quizId)
        }
    }
    
    fun setContent(content: List<LearningContent>) {
        contentList = content
        notifyDataSetChanged()
    }
}

class EmptyContentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empty_content, container, false)
    }
}

class TextContentFragment : Fragment() {
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_TEXT = "text"
        
        fun newInstance(title: String, text: String): TextContentFragment {
            val fragment = TextContentFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val binding = FragmentTextContentBinding.bind(view)
        
        arguments?.let {
            binding.contentTitle.text = it.getString(ARG_TITLE)
            binding.contentText.text = it.getString(ARG_TEXT)
        }
    }
}

class ImageTextContentFragment : Fragment() {
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_TEXT = "text"
        private const val ARG_IMAGE_URL = "image_url"
        
        fun newInstance(title: String, text: String, imageUrl: String): ImageTextContentFragment {
            val fragment = ImageTextContentFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_TEXT, text)
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val binding = FragmentImageTextContentBinding.bind(view)
        
        arguments?.let {
            binding.contentTitle.text = it.getString(ARG_TITLE)
            binding.contentText.text = it.getString(ARG_TEXT)
            
            // Load image
            Glide.with(requireContext())
                .load(it.getString(ARG_IMAGE_URL))
                .into(binding.contentImage)
        }
    }
}

class CardGridContentFragment : Fragment() {
    private lateinit var viewModel: CardGridViewModel
    
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_CARD_IDS = "card_ids"
        
        fun newInstance(title: String, cardIds: List<Long>): CardGridContentFragment {
            val fragment = CardGridContentFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putLongArray(ARG_CARD_IDS, cardIds.toLongArray())
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val binding = FragmentCardGridContentBinding.bind(view)
        
        arguments?.let {
            binding.contentTitle.text = it.getString(ARG_TITLE)
            
            val cardIds = it.getLongArray(ARG_CARD_IDS)?.toList() ?: emptyList()
            viewModel = ViewModelProvider(
                this,
                CardGridViewModelFactory(cardIds)
            ).get(CardGridViewModel::class.java)
            
            setupCardGrid(binding)
            observeViewModel(binding)
        }
    }
    
    private fun setupCardGrid(binding: FragmentCardGridContentBinding) {
        val adapter = CardGridAdapter { card ->
            findNavController().navigate(
                LearningModuleFragmentDirections.actionToCardDetail(card.id)
            )
        }
        
        binding.cardGrid.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(CardGridItemDecoration(3, dpToPx(8), true))
        }
    }
    
    private fun observeViewModel(binding: FragmentCardGridContentBinding) {
        viewModel.cards.observe(viewLifecycleOwner) { cards ->
            (binding.cardGrid.adapter as CardGridAdapter).submitList(cards)
        }
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

class QuizContentFragment : Fragment() {
    private lateinit var viewModel: QuizViewModel
    
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_QUIZ_ID = "quiz_id"
        
        fun newInstance(title: String, quizId: String): QuizContentFragment {
            val fragment = QuizContentFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_QUIZ_ID, quizId)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val binding = FragmentQuizContentBinding.bind(view)
        
        arguments?.let {
            binding.quizTitle.text = it.getString(ARG_TITLE)
            
            val quizId = it.getString(ARG_QUIZ_ID) ?: return@let
            viewModel = ViewModelProvider(
                this,
                QuizViewModelFactory(quizId)
            ).get(QuizViewModel::class.java)
            
            setupQuizView(binding)
            observeViewModel(binding)
        }
    }
    
    private fun setupQuizView(binding: FragmentQuizContentBinding) {
        binding.submitButton.setOnClickListener {
            val selectedOption = binding.optionsRadioGroup.checkedRadioButtonId
            val optionIndex = when (selectedOption) {
                R.id.option1 -> 0
                R.id.option2 -> 1
                R.id.option3 -> 2
                R.id.option4 -> 3
                else -> -1
            }
            
            if (optionIndex >= 0) {
                viewModel.submitAnswer(optionIndex)
            }
        }
        
        binding.nextQuestionButton.setOnClickListener {
            viewModel.loadNextQuestion()
            resetQuizView(binding)
        }
    }
    
    private fun observeViewModel(binding: FragmentQuizContentBinding) {
        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            binding.questionText.text = question.text
            
            // Set options
            binding.option1.text = question.options[0]
            binding.option2.text = question.options[1]
            binding.option3.text = question.options[2]
            binding.option4.text = question.options[3]
            
            // Reset selection
            binding.optionsRadioGroup.clearCheck()
            
            // Update question counter
            binding.questionCounter.text = getString(
                R.string.question_counter_format,
                viewModel.currentQuestionIndex.value?.plus(1) ?: 1,
                viewModel.totalQuestions.value ?: 0
            )
        }
        
        viewModel.answerResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                showAnswerResult(binding, result)
            }
        }
        
        viewModel.quizComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                showQuizComplete(binding)
            }
        }
    }
    
    private fun showAnswerResult(binding: FragmentQuizContentBinding, result: AnswerResult) {
        binding.resultCard.isVisible = true
        
        if (result.isCorrect) {
            binding.resultCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_answer))
            binding.resultIcon.setImageResource(R.drawable.ic_check_circle)
            binding.resultText.text = getString(R.string.correct_answer)
        } else {
            binding.resultCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.incorrect_answer))
            binding.resultIcon.setImageResource(R.drawable.ic_error)
            binding.resultText.text = getString(R.string.incorrect_answer_format, result.correctAnswer)
        }
        
        binding.submitButton.isEnabled = false
        binding.nextQuestionButton.isVisible = true
    }
    
    private fun showQuizComplete(binding: FragmentQuizContentBinding) {
        binding.quizContent.isVisible = false
        binding.quizCompleteGroup.isVisible = true
        
        val score = viewModel.score.value ?: 0
        val total = viewModel.totalQuestions.value ?: 0
        binding.scoreText.text = getString(R.string.quiz_score_format, score, total)
        
        // Set progress bar
        val percentage = if (total > 0) (score * 100) / total else 0
        binding.scoreProgressBar.progress = percentage
        binding.scorePercentage.text = "$percentage%"
    }
    
    private fun resetQuizView(binding: FragmentQuizContentBinding) {
        binding.resultCard.isVisible = false
        binding.submitButton.isEnabled = true
        binding.nextQuestionButton.isVisible = false
        binding.optionsRadioGroup.clearCheck()
    }
}
```

### 6. Learning Data Classes

```kotlin
data class LearningModule(
    val id: String,
    val title: String,
    val description: String,
    val content: List<LearningContent>,
    val isCompleted: Boolean = false
)

data class LearningContent(
    val type: ContentType,
    val title: String,
    val text: String = "",
    val imageUrl: String = "",
    val cardIds: List<Long> = emptyList(),
    val quizId: String = ""
)

enum class ContentType {
    TEXT,
    IMAGE_TEXT,
    CARD_GRID,
    QUIZ
}

data class Quiz(
    val id: String,
    val title: String,
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

data class AnswerResult(
    val isCorrect: Boolean,
    val correctAnswer: String
)
```

## Card Dictionary Styling and Animations

### 1. Card Grid Item Animation

```kotlin
class CardItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.scaleX = 0.8f
        holder.itemView.scaleY = 0.8f
        
        holder.itemView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator())
            .start()
        
        return true
    }
    
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        newHolder.itemView.alpha = 0f
        newHolder.itemView.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        
        return true
    }
}
```

### 2. Card Detail Transition

```kotlin
class CardDetailTransition : MaterialContainerTransform() {
    init {
        drawingViewId = R.id.nav_host_fragment
        duration = 300L
        scrimColor = Color.TRANSPARENT
        fadeMode = FADE_MODE_THROUGH
        fitMode = FITTING_MODE_HEIGHT
    }
}
```

### 3. Card Flip Animation

```kotlin
class CardFlipAnimation(
    private val cardView: View,
    private val frontView: View,
    private val backView: View
) {
    private val animationDuration = 300L
    
    fun flipToBack() {
        // Set up the animations
        val scale = cardView.context.resources.displayMetrics.density
        cardView.cameraDistance = 8000 * scale
        
        val firstHalf = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f).apply {
            duration = animationDuration / 2
            interpolator = AccelerateInterpolator()
        }
        
        val secondHalf = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f).apply {
            duration = animationDuration / 2
            interpolator = DecelerateInterpolator()
        }
        
        firstHalf.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                frontView.visibility = View.GONE
                backView.visibility = View.VISIBLE
                secondHalf.start()
            }
        })
        
        firstHalf.start()
    }
    
    fun flipToFront() {
        // Set up the animations
        val scale = cardView.context.resources.displayMetrics.density
        cardView.cameraDistance = 8000 * scale
        
        val firstHalf = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, -90f).apply {
            duration = animationDuration / 2
            interpolator = AccelerateInterpolator()
        }
        
        val secondHalf = ObjectAnimator.ofFloat(cardView, "rotationY", 90f, 0f).apply {
            duration = animationDuration / 2
            interpolator = DecelerateInterpolator()
        }
        
        firstHalf.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                backView.visibility = View.GONE
                frontView.visibility = View.VISIBLE
                secondHalf.start()
            }
        })
        
        firstHalf.start()
    }
}
```

### 4. Card Zoom Gesture Detector

```kotlin
class CardZoomGestureDetector(
    private val context: Context,
    private val imageView: ImageView
) {
    private val scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private val minScale = 1.0f
    private val maxScale = 3.0f
    
    init {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        return scaleGestureDetector.onTouchEvent(event)
    }
    
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            
            // Don't let the object get too small or too large
            scaleFactor = max(minScale, min(scaleFactor, maxScale))
            
            // Apply scale to image view
            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            
            return true
        }
    }
    
    fun resetZoom() {
        scaleFactor = 1.0f
        imageView.scaleX = scaleFactor
        imageView.scaleY = scaleFactor
    }
}
```

## Sample Tarot Card Data

```kotlin
object SampleTarotData {
    fun getMajorArcanaCards(): List<Card> {
        return listOf(
            Card(
                id = 1,
                deckId = 1,
                name = "The Fool",
                number = 0,
                arcanaType = "Major",
                suit = null,
                imageUrl = "https://example.com/images/rider_waite/major/fool.jpg",
                keywords = listOf(
                    CardKeyword(1, 1, "New beginnings", false),
                    CardKeyword(2, 1, "Innocence", false),
                    CardKeyword(3, 1, "Spontaneity", false),
                    CardKeyword(4, 1, "Free spirit", false),
                    CardKeyword(5, 1, "Recklessness", true),
                    CardKeyword(6, 1, "Risk-taking", true),
                    CardKeyword(7, 1, "Naivety", true)
                ),
                uprightMeaning = "The Fool represents new beginnings, having faith in the future, being inexperienced, not knowing what to expect, having beginner's luck, improvisation and believing in the universe.",
                reversedMeaning = "When reversed, the Fool suggests recklessness, risk-taking, and not understanding the repercussions of your actions. It can indicate naivety, foolishness, and poor judgment.",
                element = "Air",
                zodiac = "Uranus",
                numerology = "0",
                symbolism = "The Fool depicts a young man standing on the edge of a cliff, about to step off. He carries a white rose, symbolizing purity, and a small knapsack, representing untapped collective knowledge. The white dog at his feet symbolizes protection and loyalty. The mountains behind him represent the challenges ahead, while the sun shining down on him suggests that the divine is smiling on him and blessing his journey."
            ),
            Card(
                id = 2,
                deckId = 1,
                name = "The Magician",
                number = 1,
                arcanaType = "Major",
                suit = null,
                imageUrl = "https://example.com/images/rider_waite/major/magician.jpg",
                keywords = listOf(
                    CardKeyword(8, 2, "Manifestation", false),
                    CardKeyword(9, 2, "Power", false),
                    CardKeyword(10, 2, "Skill", false),
                    CardKeyword(11, 2, "Concentration", false),
                    CardKeyword(12, 2, "Manipulation", true),
                    CardKeyword(13, 2, "Illusion", true),
                    CardKeyword(14, 2, "Trickery", true)
                ),
                uprightMeaning = "The Magician represents manifestation, resourcefulness, power, inspired action, and the ability to create your own reality. It signifies having the tools, resources, and energy to make your dreams come true.",
                reversedMeaning = "When reversed, the Magician suggests manipulation, poor planning, and untapped talents. It can indicate using your skills for selfish gain or deception, or not making the most of your abilities.",
                element = "Air",
                zodiac = "Mercury",
                numerology = "1",
                symbolism = "The Magician stands with one arm raised toward the sky and the other pointing to the ground, representing the principle of 'as above, so below.' On the table in front of him are the four suits of the Tarot, symbolizing the four elements and the resources at his disposal. The infinity symbol above his head represents unlimited potential, while the red and white robes symbolize purity and experience."
            )
            // Add more Major Arcana cards here
        )
    }
    
    fun getMinorArcanaCards(): List<Card> {
        return listOf(
            Card(
                id = 22,
                deckId = 1,
                name = "Ace of Wands",
                number = 1,
                arcanaType = "Minor",
                suit = "Wands",
                imageUrl = "https://example.com/images/rider_waite/wands/ace.jpg",
                keywords = listOf(
                    CardKeyword(78, 22, "Creation", false),
                    CardKeyword(79, 22, "Inspiration", false),
                    CardKeyword(80, 22, "New opportunities", false),
                    CardKeyword(81, 22, "Growth", false),
                    CardKeyword(82, 22, "Delays", true),
                    CardKeyword(83, 22, "Missed opportunities", true),
                    CardKeyword(84, 22, "Lack of inspiration", true)
                ),
                uprightMeaning = "The Ace of Wands represents creation, willpower, inspiration, and desire. It signifies a new beginning filled with potential and enthusiasm. This card encourages you to follow your passion and take the first steps toward your goals.",
                reversedMeaning = "When reversed, the Ace of Wands suggests delays, lack of motivation, and missed opportunities. It can indicate creative blocks, poor timing, or a need to reevaluate your goals and approach.",
                element = "Fire",
                zodiac = null,
                numerology = "1",
                symbolism = "The Ace of Wands depicts a hand emerging from a cloud, holding a sprouting wand. The lush landscape below represents fertility and growth, while the castle in the distance symbolizes the potential for achievement and success. The wand itself represents creativity, energy, and the spark of a new idea or venture."
            ),
            Card(
                id = 36,
                deckId = 1,
                name = "Ace of Cups",
                number = 1,
                arcanaType = "Minor",
                suit = "Cups",
                imageUrl = "https://example.com/images/rider_waite/cups/ace.jpg",
                keywords = listOf(
                    CardKeyword(127, 36, "Love", false),
                    CardKeyword(128, 36, "New relationships", false),
                    CardKeyword(129, 36, "Compassion", false),
                    CardKeyword(130, 36, "Creativity", false),
                    CardKeyword(131, 36, "Emotional loss", true),
                    CardKeyword(132, 36, "Blocked creativity", true),
                    CardKeyword(133, 36, "Emptiness", true)
                ),
                uprightMeaning = "The Ace of Cups represents emotional beginnings, intuition, love, and compassion. It signifies the start of a new relationship, a deeper connection with yourself, or a creative awakening. This card encourages you to open your heart and embrace the flow of emotions.",
                reversedMeaning = "When reversed, the Ace of Cups suggests emotional blockages, emptiness, and repressed feelings. It can indicate a fear of intimacy, creative blocks, or difficulty expressing emotions. It may be time to address emotional wounds and allow yourself to be vulnerable.",
                element = "Water",
                zodiac = null,
                numerology = "1",
                symbolism = "The Ace of Cups depicts a hand emerging from a cloud, holding a cup overflowing with water. Five streams flow from the cup, representing the five senses. A dove descends toward the cup, symbolizing divine love and blessing. The cup itself represents the vessel of emotions, intuition, and the subconscious mind."
            )
            // Add more Minor Arcana cards here
        )
    }
}
```